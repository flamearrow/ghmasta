package band.mlgb.ghmasta.ui.reposearch.views

import androidx.lifecycle.LiveData
import band.mlgb.ghmasta.data.RepositoryState

/**
 * View for Repository Search.
 * aka View in MVI
 */
interface RepositorySearchView {
    fun renderer(state: RepositoryState)
    fun searchWithUserNameIntent(): LiveData<String>
    fun searchWithKeywordIntent(): LiveData<String>
    fun clearResultIntent(): LiveData<Unit>

    // Note: this three are required on View because the loading/error state needs to be obtained
    // from PagingDataAdapter, which is initialized in View.
    // For regular MVI pattern, the error/loading state is originated from Repository
    fun loadingIntent(): LiveData<Unit>
    fun initializedIntent(): LiveData<Unit>
    fun errorIntent(): LiveData<Throwable>
}