package band.mlgb.ghmasta.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.paging.*
import androidx.room.withTransaction
import band.mlgb.ghmasta.data.dao.GHMastaDB
import band.mlgb.ghmasta.data.model.Repository
import band.mlgb.ghmasta.network.GithubApi
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Repository for [Repository], providing pager for Repository.
 * aka Interactor in MVI pattern
 */
@Singleton
@ExperimentalPagingApi
class RepositoryRepository @Inject constructor(
    private val githubApi: GithubApi,
    private val ghMastaDB: GHMastaDB
) {

    // Used by MVVM ViewModel
    fun searchRepositoryWithUserName(userName: String): LiveData<PagingData<Repository>> {
        // need to create a new Pager each time because the search query is different
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = RepositoryMediator(
                RepositoryMediator.SearchType.USER_NAME,
                userName,
                githubApi,
                ghMastaDB
            )
        ) {
            ghMastaDB.getRepositoryDao().getRepositoriesOfUser(userName)
        }.liveData
    }

    fun searchRepositoryWithKeyword(keyword: String): LiveData<PagingData<Repository>> {
        // need to create a new Pager each time because the search query is different
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = RepositoryMediator(
                RepositoryMediator.SearchType.REPOSITORY_KEYWORD,
                keyword,
                githubApi,
                ghMastaDB
            )
        ) {
            // important: when search sql with 'like', the query needs to be pre/append with % to be a wildcard matching
            ghMastaDB.getRepositoryDao().getRepositories("%${keyword.replace(' ', '%')}%")
        }.liveData
    }

    // Used by MVI Presenter
    fun getResultsWithUserName(userName: String): LiveData<RepositoryState> =
        searchRepositoryWithUserName(userName).switchMap {
            liveData {
                emit(RepositoryState.DataState(it) as RepositoryState)
            }
        }

    fun getResultsWithKeyword(keyWord: String): LiveData<RepositoryState> =
        searchRepositoryWithKeyword(keyWord).switchMap {
            liveData {
                emit(RepositoryState.DataState(it) as RepositoryState)
            }
        }

    suspend fun deleteResults() {
        ghMastaDB.withTransaction {
            ghMastaDB.getRepositoryPageKeyDao().clearRepositoryPageKeys()
            ghMastaDB.getRepositoryDao().clearRepositories()
            ghMastaDB.getUserDao().clearUsers()
        }
    }

    companion object {
        // # of items per page to load from PagingSource
        private const val PAGE_SIZE = 5
    }
}