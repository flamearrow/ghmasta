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
}