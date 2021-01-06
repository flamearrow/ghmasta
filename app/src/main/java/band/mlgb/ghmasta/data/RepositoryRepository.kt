package band.mlgb.ghmasta.data

import androidx.lifecycle.LiveData
import androidx.paging.*
import band.mlgb.ghmasta.data.dao.GHMastaDB
import band.mlgb.ghmasta.data.model.Repository
import band.mlgb.ghmasta.network.GithubApi
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Repository for [Repository], providing pager for Repository
 */
@Singleton
class RepositoryRepository @Inject constructor(
    private val githubApi: GithubApi,
    private val ghMastaDB: GHMastaDB
) {

    @ExperimentalPagingApi
    fun searchRepositoryWithUserId(userLoginName: String): LiveData<PagingData<Repository>> {
        // need to create a new Pager each time because the search query is different
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = RepositoryMediator(
                userLoginName,
                githubApi,
                ghMastaDB
            )
        ) {
            ghMastaDB.getRepositoryDao().getRepositoriesOfUser(userLoginName)
        }.liveData
    }

    companion object {
        // # of items per page to load from PagingSource
        private const val PAGE_SIZE = 5
    }
}