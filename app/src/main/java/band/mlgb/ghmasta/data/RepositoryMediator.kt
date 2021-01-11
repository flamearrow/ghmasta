package band.mlgb.ghmasta.data

import android.net.UrlQuerySanitizer
import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import band.mlgb.ghmasta.data.dao.GHMastaDB
import band.mlgb.ghmasta.data.model.Repository
import band.mlgb.ghmasta.data.model.RepositoryPageKey
import band.mlgb.ghmasta.data.model.RepositorySearchResponse
import band.mlgb.ghmasta.network.GithubApi

/**
 * The Remote mediator fires request to github api and uses the 'link' header of the response for pagination.
 * See [RepositoryMediator.getPageInfo] for details on resolving link header.
 */
@ExperimentalPagingApi
class RepositoryMediator(
    private val searchType: SearchType,
    private val query: String,
    private val githubApi: GithubApi,
    private val ghMastaDB: GHMastaDB,
) : RemoteMediator<Int, Repository>() {

    enum class SearchType {
        USER_NAME, // "users/${query}/repos"
        REPOSITORY_KEYWORD // "repositories?q=${query}&sort=stars"
    }

    // check pageToRequest, decide if we want to fire API request
    //   if refresh, fire request regardless
    //      fire a request to pull the repos
    //      save repo in repo table
    //      read page number, next page id from response header
    //      save <repo, page number, next page number, prev page number> to repo_page_db
    //      calculate endOfPaginationReached
    //          if current page = 1, true, don't fire any request
    //          return Success(endOfPaginationReached)
    //   otherwise
    //      if current page = 1, don't fire any request, return Success with endOfPaginationReached = true
    //      if current page = last page, don't fire any request, return Success with endOfPaginationReached = true
    //
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Repository>
    ): MediatorResult {
        return when (loadType) {
            LoadType.REFRESH -> {
                mediatorLog("REFRESH")
                // 1 or page of current item
                val page = state.anchorPosition?.let {
                    state.closestItemToPosition(it)?.let { repository ->
                        ghMastaDB.getRepositoryPageKeyDao()
                            .getRepositoryPageKey(repository.id)?.currentPage
                    }
                } ?: GITHUB_STARTING_PAGE_INDEX
                fireRequestForPageAndUpdateDB(page, true)
            }

            LoadType.APPEND -> {
                mediatorLog("APPEND")
                // next page of last item
                state.lastItemOrNull()?.id?.let { repositoryId ->
                    mediatorLog("searching for repositoryId $repositoryId to append")
                    ghMastaDB.getRepositoryPageKeyDao().getRepositoryPageKey(
                        repositoryId
                    )
                }?.let { repositoryPageKey ->
                    return if (repositoryPageKey.currentPage == repositoryPageKey.lastPage) {
                        // already reaches end, don't pull
                        mediatorLog("reaching end, don't pull more pages, currentPage: ${repositoryPageKey.currentPage}")
                        MediatorResult.Success(endOfPaginationReached = true)
                    } else {
                        // not reaching last, fire request for nextPage
                        mediatorLog("not reaching end, pulling more pages, currentPage: ${repositoryPageKey.currentPage}, pulling nextPage: ${repositoryPageKey.nextPage}")
                        fireRequestForPageAndUpdateDB(repositoryPageKey.nextPage)
                    }
                } ?: run {
                    mediatorLog("Failed to resolve PagingState $state")
                    MediatorResult.Error(IllegalStateException("Failed to resolve PagingState $state"))
                }
            }

            LoadType.PREPEND -> {
                mediatorLog("PREPEND")
                mediatorLog("first item or null: ${state.firstItemOrNull()}")
                // previous page of 1st item
                state.firstItemOrNull()?.id?.let { repositoryId ->
                    mediatorLog("searching for repositoryId $repositoryId to prepend")
                    ghMastaDB.getRepositoryPageKeyDao().getRepositoryPageKey(
                        repositoryId
                    )
                }?.let { repositoryPageKey ->
                    return if (repositoryPageKey.currentPage == repositoryPageKey.firstPage) {
                        // already reaches head, don't pull
                        mediatorLog("reaching head, don't pull more pages, currentPage: ${repositoryPageKey.currentPage}")
                        MediatorResult.Success(endOfPaginationReached = true)
                    } else {
                        // not reaching first, fire request for previousPage
                        mediatorLog("not reaching head, pulling more pages, currentPage: ${repositoryPageKey.currentPage}, pulling previousPage: ${repositoryPageKey.previousPage}")
                        fireRequestForPageAndUpdateDB(repositoryPageKey.previousPage)
                    }
                } ?: run {
                    mediatorLog("Failed to resolve PagingState $state")
                    MediatorResult.Error(IllegalStateException("Failed to resolve PagingState $state"))
                }
            }
        }

    }

    private fun mediatorLog(msg: String) {
        Log.d(TAG, msg)
    }

    data class PageInfo(
        val nextPage: Int,
        val previousPage: Int,
        val firstPage: Int,
        val lastPage: Int
    )


    // fire request to pull repository of a user at current page,
    // upon response save Repository from the response and RepositoryPageKey from header
    // once we hit this, endOfPaging is not reached
    private suspend fun fireRequestForPageAndUpdateDB(
        currentPage: Int,
        clearRepo: Boolean = false
    ): MediatorResult {
        try {
            when (searchType) {
                SearchType.USER_NAME -> githubApi.fetchReposForUserResponse(query, currentPage)
                SearchType.REPOSITORY_KEYWORD -> githubApi.searchRepoWithKeyword(query, currentPage)
            }.let { response ->
                if (response.isSuccessful) {
                    response.headers()[LINK]?.let { linkHeader ->
                        @Suppress("UNCHECKED_CAST")
                        when (searchType) {
                            SearchType.USER_NAME -> (response.body() as List<Repository>)
                            SearchType.REPOSITORY_KEYWORD -> (response.body() as RepositorySearchResponse).items
                        }.let { repositories ->
                            mediatorLog("received ${repositories.size} new repos, clearRepo: $clearRepo, searchType: $searchType")
                            insertRepositoriesIntoDB(
                                repositories,
                                linkHeader,
                                currentPage,
                                clearRepo
                            )
                        }
                        return MediatorResult.Success(endOfPaginationReached = false)
                    } ?: run {
                        // no link header found, fail
                        mediatorLog("no header")
                        return MediatorResult.Error(IllegalStateException("no link header returned"))
                    }
                } else {
                    mediatorLog("request failed ${response.message()}")
                    return MediatorResult.Error(IllegalStateException("response not successful"))
                }
            }
        } catch (e: Exception) {
            mediatorLog("failed to request for page $currentPage with $searchType: $e")
            return MediatorResult.Error(e)
        }
    }

    private suspend fun insertRepositoriesIntoDB(
        repositories: List<Repository>,
        linkHeader: String,
        currentPage: Int,
        clearRepo: Boolean
    ) {
        val (nextPage, previousPage, firstPage, lastPage) = getPageInfo(
            linkHeader
        )
        val users = repositories.map { it.owner }
        val repositoryPageKeys = repositories.map {
            RepositoryPageKey(
                it.id,
                currentPage,
                nextPage,
                previousPage,
                firstPage,
                lastPage
            )
        }
        ghMastaDB.withTransaction {
            if (clearRepo) {
                ghMastaDB.getUserDao().clearUsers()
                ghMastaDB.getRepositoryDao().clearRepositories()
                ghMastaDB.getRepositoryPageKeyDao()
                    .clearRepositoryPageKeys()
            }
            ghMastaDB.getUserDao().insertUsers(users)
            ghMastaDB.getRepositoryDao()
                .insertRepositories(repositories)
            ghMastaDB.getRepositoryPageKeyDao()
                .insertRepositoryPageKeys(repositoryPageKeys)
        }
    }

    // for first page, has next and last, set first to 1 and prev to 1
    // for middle pages, has prev, next, last and first
    // for last page, has prev and first, set next to prev+1 and last to prev+1
    // samples:
    //   response for first page
    //        "<https://api.github.com/user/4720570/repos?per_page=10&page=2>; rel=\"next\", <https://api.github.com/user/4720570/repos?per_page=10&page=5>; rel=\"last\"" // no prev and last
    //   response for middle pages
    //        "<https://api.github.com/user/4720570/repos?per_page=10&page=3>; rel=\"prev\", <https://api.github.com/user/4720570/repos?page=4&per_page=10>; rel=\"next\", <https://api.github.com/user/4720570/repos?per_page=10&page=5>; rel=\"last\", <https://api.github.com/user/4720570/repos?per_page=10&page=1>; rel=\"first\""
    //   response for last page
    //        <https://api.github.com/user/4720570/repos?per_page=10&page=4>; rel="prev", <https://api.github.com/user/4720570/repos?per_page=10&page=1>; rel="first"
    private fun getPageInfo(linkHeader: String): PageInfo {
        var nextPage = 0
        var previousPage = 0
        var firstPage = 0
        var lastPage = 0

        linkHeader.split(",").forEach { urlRelPairStr ->
            urlRelPairStr.split(";").let { urlRelPair ->
                urlRelPair[1].trim().let { rel ->
                    rel.substring(5, rel.length - 1).let { relValue ->
                        urlRelPair[0].trim().let { url ->
                            // relValue : page
                            // first: 1, last: 5, prev: 4, next: 5
                            val page =
                                UrlQuerySanitizer(url.substring(1, url.length - 1)).getValue(PAGE)
                                    .toInt()
                            when (relValue) {
                                FIRST -> firstPage = page
                                LAST -> lastPage = page
                                PREV -> previousPage = page
                                NEXT -> nextPage = page
                            }
                        }
                    }
                }
            }
        }

        // first page
        if (firstPage == 0) {
            firstPage = 1
            previousPage = 1
        }

        // last page
        if (lastPage == 0) {
            nextPage = previousPage + 1
            lastPage = previousPage + 1
        }

        val ret = PageInfo(nextPage, previousPage, firstPage, lastPage)
        mediatorLog("new page info: $ret")
        return ret
    }


    companion object {
        private const val GITHUB_STARTING_PAGE_INDEX = 1
        private const val LINK = "link"
        private const val PREV = "prev"
        private const val NEXT = "next"
        private const val LAST = "last"
        private const val FIRST = "first"
        private const val PAGE = "page"
        private val TAG = RepositoryMediator::class.java.simpleName
    }
}
