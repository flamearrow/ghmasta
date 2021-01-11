package band.mlgb.ghmasta.ui.reposearch.mvvm

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import band.mlgb.ghmasta.databinding.FragmentRepositorySearchBinding
import band.mlgb.ghmasta.ui.reposearch.SearchResultPagingAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * View for Repository search MVVM
 */
@ExperimentalPagingApi
@AndroidEntryPoint
class RepositorySearchMVVMFragment : Fragment() {

    @Inject
    lateinit var viewModel: RepositorySearchMVVMViewModel

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
                    true -> viewModel.userIdLD
                    false -> viewModel.repositoryKeyword
                }.postValue(searchText.toString())
            }
        }

        binding.filter.setOnCheckedChangeListener { _, _ ->
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    viewModel.clearDB()
                }
                withContext(Dispatchers.Main) {
                    toggleUIVisibility(VisibleUIComponent.RESULTS)
                }
            }
        }

        // The listener gets result form both paging source(CombinedLoadStates.source)
        // and remote mediator(CombinedLoadStates.mediator)
        // use refresh/append/prepend field to get combined results
        // can also access dedicated results from
        reposAdapter.addLoadStateListener { loadState ->
            checkLoadState(loadState.refresh, REFRESH)
            checkLoadState(loadState.append, APPEND)
            checkLoadState(loadState.prepend, PREPEND)
        }

        binding.results.adapter = reposAdapter

        viewModel.reposResultLive.observe(viewLifecycleOwner) { reposList ->
            lifecycleScope.launch {
                reposAdapter.submitData(reposList)
            }
        }

        return binding.root
    }

    enum class VisibleUIComponent {
        RESULTS, PROGRESSBAR, RETRYBUTTON
    }

    private fun toggleUIVisibility(component: VisibleUIComponent) {
        when (component) {
            VisibleUIComponent.RESULTS -> {
                binding.results.visibility = View.VISIBLE
                binding.progressBar.visibility = View.INVISIBLE
                binding.retryButton.visibility = View.INVISIBLE
            }
            VisibleUIComponent.PROGRESSBAR -> {
                binding.results.visibility = View.INVISIBLE
                binding.progressBar.visibility = View.VISIBLE
                binding.retryButton.visibility = View.INVISIBLE
            }
            VisibleUIComponent.RETRYBUTTON -> {
                binding.results.visibility = View.INVISIBLE
                binding.progressBar.visibility = View.INVISIBLE
                binding.retryButton.visibility = View.VISIBLE
            }
        }
    }

    // only flip the UI components with type refresh, append and prepend UI errors will be handled
    // by header and footer
    private fun checkLoadState(
        state: LoadState,
        type: String,
    ) {
        when (state) {
            is LoadState.NotLoading -> {
                repositoryLog("$type not loading")
                if (type == REFRESH) {
                    toggleUIVisibility(VisibleUIComponent.RESULTS)
                }
            }
            LoadState.Loading -> {
                repositoryLog("$type is loading")
                if (type == REFRESH) {
                    toggleUIVisibility(VisibleUIComponent.PROGRESSBAR)
                }
            }
            is LoadState.Error -> {
                showError("Failed to load data for $type", state.error)
                if (type == REFRESH) {
                    toggleUIVisibility(VisibleUIComponent.RETRYBUTTON)
                }
            }
        }
    }

    private fun showError(msg: String, t: Throwable) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        Log.e(TAG, "error occurred: $t")
    }

    private fun repositoryLog(msg: String) {
        Log.d(TAG, msg)
    }

    companion object {
        private val TAG = RepositorySearchMVVMFragment::class.java.simpleName
        private const val REFRESH = "REFRESH"
        private const val APPEND = "APPEND"
        private const val PREPEND = "PREPEND"
    }


}