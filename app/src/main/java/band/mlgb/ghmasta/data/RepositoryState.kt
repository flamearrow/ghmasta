package band.mlgb.ghmasta.data

import androidx.paging.PagingData
import band.mlgb.ghmasta.data.model.Repository

/**
 * View state used for MVI
 */
sealed class RepositoryState {
    // when no search is performed and database is clean
    object InitializedState : RepositoryState()

    // when a search is on going
    object LoadingState : RepositoryState()

    // when search result returns
    data class DataState(val data: PagingData<Repository>) : RepositoryState()

    // when search errors
    data class ErrorState(val throwable: Throwable) : RepositoryState()
}