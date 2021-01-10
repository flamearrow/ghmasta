package band.mlgb.ghmasta.ui.repomvi

import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import band.mlgb.ghmasta.data.RepositoryRepository
import band.mlgb.ghmasta.data.RepositoryState
import band.mlgb.ghmasta.ui.views.RepositorySearchView
import dagger.hilt.android.scopes.ActivityRetainedScoped
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
    }
}