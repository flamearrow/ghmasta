package band.mlgb.ghmasta.ui.home

import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import band.mlgb.ghmasta.data.RepositoryRepository
import band.mlgb.ghmasta.data.model.Repository
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
@ExperimentalPagingApi
class HomeViewModel @Inject constructor(
    private val repositoryRepo: RepositoryRepository,
) : ViewModel() {

    val userIdLD: MutableLiveData<String> = MutableLiveData()

    val repositoryKeyword: MutableLiveData<String> = MutableLiveData()

    private val userReposLive: LiveData<PagingData<Repository>> = userIdLD.switchMap { userIdLD ->
        liveData {
            emitSource(repositoryRepo.searchRepositoryWithUserId(userIdLD).cachedIn(viewModelScope))
        }
    }

    private val keywordSearchReposLive: LiveData<PagingData<Repository>> =
        repositoryKeyword.switchMap { repositoryKeyword ->
            liveData {
                emitSource(
                    repositoryRepo.searchRepositoryWithKeyword(repositoryKeyword)
                        .cachedIn(viewModelScope)
                )
            }
        }


    val reposResultLive = MediatorLiveData<PagingData<Repository>>().also { reposResult ->
        { newPagingData: PagingData<Repository> ->
            reposResult.value = newPagingData
        }.let { newPagingDataObserver ->
            reposResult.addSource(userReposLive, newPagingDataObserver)
            reposResult.addSource(keywordSearchReposLive, newPagingDataObserver)
        }
    }
}