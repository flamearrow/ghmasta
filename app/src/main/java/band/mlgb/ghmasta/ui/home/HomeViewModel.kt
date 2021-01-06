package band.mlgb.ghmasta.ui.home

import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import band.mlgb.ghmasta.data.RepositoryRepository
import band.mlgb.ghmasta.data.dao.RepositoryDao
import band.mlgb.ghmasta.data.dao.RepositoryPageKeyDao
import band.mlgb.ghmasta.data.dao.UserDao
import band.mlgb.ghmasta.data.model.Repository
import band.mlgb.ghmasta.network.GithubApi
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ActivityRetainedScoped
class HomeViewModel @Inject constructor(
    private val githubApi: GithubApi,
    private val repositoryRepo: RepositoryRepository,
    private val userDao: UserDao,
    private val repositoryDao: RepositoryDao,
    private val repositoryPageKeyDao: RepositoryPageKeyDao
) : ViewModel() {

    val userIdLD: MutableLiveData<String> = MutableLiveData()

    val repos: LiveData<List<Repository>> = userIdLD.switchMap { userId ->
        liveData {
            val response0 = githubApi.fetchReposForUserResponse(userId, 1)
            val response1 = githubApi.fetchReposForUserResponse(userId, 3)
            val response = githubApi.fetchReposForUserResponse(userId, 10)
//            response.headers().get("link")
//            <https://api.github.com/user/4720570/repos?page=1>; rel="prev", <https://api.github.com/user/4720570/repos?page=1>; rel="first"
            emit(response0.body()!!)
            emit(response1.body()!!)
            emit(response.body()!!)
//            emit(githubApi.fetchReposForUser(userId))
        }
    }


    @ExperimentalPagingApi
    val reposLive: LiveData<PagingData<Repository>> = userIdLD.switchMap { userIdLD ->
        liveData {
            emitSource(repositoryRepo.searchRepositoryWithUserId(userIdLD).cachedIn(viewModelScope))

        }
    }

    @ExperimentalPagingApi
    suspend fun searchResultLiveData(userId: String): LiveData<PagingData<Repository>> =
        withContext(Dispatchers.IO) {
            repositoryRepo.searchRepositoryWithUserId(userId)
        }
}