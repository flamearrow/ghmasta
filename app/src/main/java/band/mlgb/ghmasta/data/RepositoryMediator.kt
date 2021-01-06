package band.mlgb.ghmasta.data

import android.net.UrlQuerySanitizer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import band.mlgb.ghmasta.data.dao.GHMastaDB
import band.mlgb.ghmasta.data.model.Repository
import band.mlgb.ghmasta.data.model.RepositoryPageKey
import band.mlgb.ghmasta.network.GithubApi
import band.mlgb.ghmasta.utils.bglmLog


@ExperimentalPagingApi
class RepositoryMediator(
    private val userLoginName: String,
    private val githubApi: GithubApi,
    private val ghMastaDB: GHMastaDB,
) : RemoteMediator<Int, Repository>() {
//    fun printPages(state: PagingState<Int, Repository>) {
//        bglmLog("---logging current state---")
//        bglmLog("${state.pages.size} pages load")
//        state.pages.forEach { page->
//            bglmLog("${page.si}${page.nextKey}")
//        }
//
//        bglmLog("---end of logging current state---")
//    }
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
                bglmLog("REFRESH")
                // 1 or page of current item
                val page = state.anchorPosition?.let {
                    state.closestItemToPosition(it)?.let { repository ->
                        ghMastaDB.getRepositoryPageKeyDao()
                            .getRepositoryPageKey(repository.id)?.currentPage
                    }
                } ?: 1
                fireRequestForPage(page, true)
            }

            LoadType.APPEND -> {
                bglmLog("APPEND")

                // next page of last item
                state.lastItemOrNull()?.id?.let { repositoryId ->
                    bglmLog("searching for repositoryId $repositoryId to append")
                    ghMastaDB.getRepositoryPageKeyDao().getRepositoryPageKey(
                        repositoryId
                    )
                }?.let { repositoryPageKey ->
                    return if (repositoryPageKey.currentPage == repositoryPageKey.lastPage) {
                        // already reaches end, don't pull
                        bglmLog("reaching end, don't pull more pages, currentPage: ${repositoryPageKey.currentPage}")
                        MediatorResult.Success(endOfPaginationReached = true)
                    } else {
                        // not reaching last, fire request for nextPage
                        bglmLog("not reaching end, pulling more pages, currentPage: ${repositoryPageKey.currentPage}, pulling nextPage: ${repositoryPageKey.nextPage}")
                        fireRequestForPage(repositoryPageKey.nextPage)
                    }
                } ?: run {
                    bglmLog("Failed to resolve PagingState $state")
                    MediatorResult.Error(IllegalStateException("Failed to resolve PagingState $state"))
                }
            }

            LoadType.PREPEND -> {
                bglmLog("PREPEND")
                bglmLog("first item or null: ${state.firstItemOrNull()}")
                // previous page of 1st item
                state.firstItemOrNull()?.id?.let { repositoryId ->
                    bglmLog("searching for repositoryId $repositoryId to prepend")
                    ghMastaDB.getRepositoryPageKeyDao().getRepositoryPageKey(
                        repositoryId
                    )
                }?.let { repositoryPageKey ->
                    return if (repositoryPageKey.currentPage == repositoryPageKey.firstPage) {
                        // already reaches head, don't pull
                        bglmLog("reaching head, don't pull more pages, currentPage: ${repositoryPageKey.currentPage}")
                        MediatorResult.Success(endOfPaginationReached = true)
                    } else {
                        // not reaching first, fire request for previousPage
                        bglmLog("not reaching head, pulling more pages, currentPage: ${repositoryPageKey.currentPage}, pulling previousPage: ${repositoryPageKey.previousPage}")
                        fireRequestForPage(repositoryPageKey.previousPage)
                    }
                } ?: run {
                    bglmLog("Failed to resolve PagingState $state")
                    MediatorResult.Error(IllegalStateException("Failed to resolve PagingState $state"))
                }
            }
        }

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
    private suspend fun fireRequestForPage(
        currentPage: Int,
        clearRepo: Boolean = false
    ): MediatorResult {
        try {
            githubApi.fetchReposForUserResponse(userLoginName, currentPage).let { response ->
                if (response.isSuccessful) {
                    bglmLog("received ${response.body()!!.size} repos for page $currentPage")
                    response.headers()[LINK]?.let { linkHeader ->
                        val (nextPage, previousPage, firstPage, lastPage) = getPageInfo(linkHeader)
                        response.body()?.let { repositories ->
                            val users = repositories.map { it.owner }
                            val repositoryPageKeys = repositories.map {
//                                bglmLog("inserting repo with id ${it.id} with current: $currentPage, next: $nextPage, previous: $previousPage, first: $firstPage, last: $lastPage")
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
                                    ghMastaDB.getRepositoryPageKeyDao().clearRepositoryPageKeys()
                                }
                                ghMastaDB.getUserDao().insertUsers(users)
                                ghMastaDB.getRepositoryDao().insertRepositories(repositories)
                                ghMastaDB.getRepositoryPageKeyDao()
                                    .insertRepositoryPageKeys(repositoryPageKeys)
                            }
                        }
                        return MediatorResult.Success(endOfPaginationReached = false)
                    } ?: run {
                        // no link header found, fail
                        bglmLog("no header")
                        return MediatorResult.Error(IllegalStateException("no link header returned"))
                    }
                } else {
                    bglmLog("request failed ${response.message()}")
                    return MediatorResult.Error(IllegalStateException("response not successful"))
                }
            }
        } catch (e: Exception) {
            bglmLog("fucked: $e")
            return MediatorResult.Error(e)
        }
    }

    // sample header:
    //    "<https://api.github.com/user/4720570/repos?per_page=10&page=2>; rel=\"prev\", <https://api.github.com/user/4720570/repos?per_page=10&page=4>; rel=\"next\", <https://api.github.com/user/4720570/repos?per_page=10&page=5>; rel=\"last\", <https://api.github.com/user/4720570/repos?per_page=10&page=1>; rel=\"first\""
    private fun getPageInfo(linkHeader: String): PageInfo {
        var nextPage = 0
        var previousPage = 0
        var firstPage = 0
        var lastPage = 0

        // for first page, has next and last, set first to 1 and prev to 1
        // for middle pages, has prev, next, last and first
        // for last page, has prev, last and first, set next to last
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
        if (nextPage == 0) {
            nextPage = lastPage
        }

        return PageInfo(nextPage, previousPage, firstPage, lastPage)
    }


    companion object {
        private const val GITHUB_STARTING_PAGE_INDEX = 1
        private const val LINK = "link"
        private const val PREV = "prev"
        private const val NEXT = "next"
        private const val LAST = "last"
        private const val FIRST = "first"
        private const val PAGE = "page"
    }
}

//fun main() {
//    val responseFirst =
//        "<https://api.github.com/user/4720570/repos?per_page=10&page=2>; rel=\"next\", <https://api.github.com/user/4720570/repos?per_page=10&page=5>; rel=\"last\""
//    val responseMiddle =
//        "<https://api.github.com/user/4720570/repos?per_page=10&page=223>; rel=\"prev\", <https://api.github.com/user/4720570/repos?page=4&per_page=10>; rel=\"next\", <https://api.github.com/user/4720570/repos?per_page=10&page=5>; rel=\"last\", <https://api.github.com/user/4720570/repos?per_page=10&page=1>; rel=\"first\""
//    val responseLast =
//        "<https://api.github.com/user/4720570/repos?per_page=10&page=5>; rel=\"prev\", <https://api.github.com/user/4720570/repos?per_page=10&page=5>; rel=\"last\", <https://api.github.com/user/4720570/repos?per_page=10&page=1>; rel=\"first\""
//
//
//
//    responseMiddle.split(",").forEach { urlRelPairStr ->
//        urlRelPairStr.split(";").let { urlRelPair ->
//            urlRelPair[1].trim().let { rel ->
//                rel.substring(5, rel.length - 1).let { relValue ->
//                    urlRelPair[0].trim().let { url ->
//                        // relValue : page
//                        // first: 1, last: 5, prev: 4, next: 5
//                        val page: String = UrlQuerySanitizer(url.substring(1, url.length - 1)).getValue("page")
//
//                        println("$relValue : $page")
//                    }
//                }
//            }
//        }
//    }
//}