package band.mlgb.ghmasta.ui.reposearch.mvi

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import band.mlgb.ghmasta.data.RepositoryState
import band.mlgb.ghmasta.databinding.FragmentRepositorySearchBinding
import band.mlgb.ghmasta.ui.reposearch.SearchResultPagingAdapter
import band.mlgb.ghmasta.ui.reposearch.views.RepositorySearchView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalPagingApi
@AndroidEntryPoint
class RepositorySearchMVIFragment : Fragment(), RepositorySearchView {

    @Inject
    lateinit var repositorySearchMVVMViewModel: RepositorySearchMVIPresenter

    @Inject
    lateinit var reposAdapter: SearchResultPagingAdapter

    lateinit var binding: FragmentRepositorySearchBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRepositorySearchBinding.inflate(inflater, container, false)
        binding.onClickListener = View.OnClickListener {
            binding.searchText.text?.let { searchText ->
                when (binding.radioName.isChecked) {
                    true -> userName
                    false -> repositoryKeyword
                }.postValue(searchText.toString())
            }
        }

        binding.filter.setOnCheckedChangeListener { _, _ ->
            clearResult.postValue(Unit)
        }

        // The listener gets result form both paging source(CombinedLoadStates.source)
        // and remote mediator(CombinedLoadStates.mediator)
        // use refresh/append/prepend field to get combined results
        // can also access dedicated results from
        reposAdapter.addLoadStateListener { loadState ->
            if (loadState.refresh is LoadState.NotLoading) {
                initialized.postValue(Unit)
            }

            // only toggle loading UI when refresh is loading
            if (loadState.refresh == LoadState.Loading) {
                loading.postValue(Unit)
            }

            // toggle error UI when any error happens in refresh/append/prepend
            (loadState.refresh as? LoadState.Error ?: loadState.append as? LoadState.Error
            ?: loadState.prepend as? LoadState.Error)?.let { loadError ->
                error.postValue(loadError.error)
            }

        }

        binding.results.adapter = reposAdapter

        repositorySearchMVVMViewModel.bind(this, this)

        return binding.root
    }

    private val userName = MutableLiveData<String>()
    private val repositoryKeyword = MutableLiveData<String>()
    private val clearResult = MutableLiveData<Unit>()

    // Note: these three are required on View because the loading/error state needs to be obtained
    // from PagingDataAdapter, which is initialized in View.
    // For regular MVI pattern, the error/loading state is originated from Repository
    private val loading = MutableLiveData<Unit>()
    private val initialized = MutableLiveData<Unit>()
    private val error = MutableLiveData<Throwable>()

    override fun renderer(state: RepositoryState) {
        when (state) {
            RepositoryState.InitializedState -> {
                repositoryLog("initialized")
                binding.results.visibility = View.VISIBLE
                binding.progressBar.visibility = View.INVISIBLE
                binding.retryButton.visibility = View.INVISIBLE
            }
            is RepositoryState.DataState -> {
                repositoryLog("new data: ${state.data}")
                lifecycleScope.launch {
                    reposAdapter.submitData(state.data)
                }
            }
            is RepositoryState.ErrorState -> {
                repositoryLog("refresh error")
                binding.results.visibility = View.INVISIBLE
                binding.progressBar.visibility = View.INVISIBLE
                binding.retryButton.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "error occurred: ${state.throwable}")

            }
            RepositoryState.LoadingState -> {
                repositoryLog("refresh is loading")
                binding.results.visibility = View.INVISIBLE
                binding.progressBar.visibility = View.VISIBLE
                binding.retryButton.visibility = View.INVISIBLE
            }
        }
    }

    override fun searchWithUserNameIntent(): LiveData<String> = userName

    override fun searchWithKeywordIntent(): LiveData<String> = repositoryKeyword

    override fun clearResultIntent(): LiveData<Unit> = clearResult

    override fun loadingIntent(): LiveData<Unit> = loading

    override fun initializedIntent(): LiveData<Unit> = initialized

    override fun errorIntent(): LiveData<Throwable> = error

    private fun repositoryLog(msg: String) {
        Log.d(TAG, msg)
    }

    companion object {
        private val TAG = RepositorySearchMVIFragment::class.java.simpleName
    }
}