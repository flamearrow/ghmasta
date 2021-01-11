package band.mlgb.ghmasta.ui.reposearch.mvi

import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import band.mlgb.ghmasta.data.RepositoryRepository
import band.mlgb.ghmasta.data.RepositoryState
import band.mlgb.ghmasta.ui.reposearch.views.RepositorySearchView
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ActivityRetainedScoped
@ExperimentalPagingApi
class RepositorySearchMVIPresenter
@Inject constructor(
    private val repositoryRepo: RepositoryRepository
) : ViewModel() {
    private lateinit var reposResultLive: MediatorLiveData<RepositoryState>

    // Usually in MVI a presenter needs to have bind() and unbind() to clear resource.
    // It's not required in ViewModel as its ActivityRetainedScoped.
    // Ideally for ViewModel this could be injected with auto factory
    @Suppress("COMPATIBILITY_WARNING")
    fun bind(view: RepositorySearchView, lifecycleOwner: LifecycleOwner) {


        // search Keyword or search repo user name
        // View triggers a search keyword, post value to its livedata, observed by Presenter,
        // presenter use the keyword to query repository/interactor, get state, send state back to view
        // View---searchKeyword--->Presenter---searchKeyword--->Repository---state--->Presenter---state---->View
        reposResultLive = MediatorLiveData<RepositoryState>().also { reposResult ->
            { repositoryState: RepositoryState ->
                reposResult.value = repositoryState
            }.let { newPagingDataObserver ->
                reposResult.addSource(
                    view.searchWithUserNameIntent().switchMap { userName ->
                        repositoryRepo.getResultsWithUserName(userName)
                    }, newPagingDataObserver
                )
                reposResult.addSource(
                    view.searchWithKeywordIntent().switchMap { repositoryKeyword ->
                        repositoryRepo.getResultsWithKeyword(repositoryKeyword)
                    }, newPagingDataObserver
                )
            }
        }

        reposResultLive.observe(lifecycleOwner) {
            view.renderer(it)
        }


        // clear
        view.clearResultIntent().observe(lifecycleOwner) {
            viewModelScope.launch {
                withContext((Dispatchers.IO)) {
                    repositoryRepo.deleteResults()
                }
                withContext(Dispatchers.Main) {
                    view.renderer(RepositoryState.InitializedState)
                }

            }

        }

        // loading
        view.loadingIntent().observe(lifecycleOwner) {
            view.renderer(RepositoryState.LoadingState)
        }

        // initialized
        view.initializedIntent().observe(lifecycleOwner) {
            view.renderer(RepositoryState.InitializedState)
        }

        // error
        view.errorIntent().observe(lifecycleOwner) { throwable ->
            view.renderer(RepositoryState.ErrorState(throwable))
        }

    }
}