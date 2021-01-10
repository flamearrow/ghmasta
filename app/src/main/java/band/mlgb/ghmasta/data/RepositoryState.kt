package band.mlgb.ghmasta.data

import androidx.paging.PagingData
import band.mlgb.ghmasta.data.model.Repository

/**
 * View state used for MVI
 */
sealed class RepositoryState {
    object LoadingState : RepositoryState()
    data class DataState(val data: PagingData<Repository>) : RepositoryState()
    data class ErrorState(val exception: Exception) : RepositoryState()
}