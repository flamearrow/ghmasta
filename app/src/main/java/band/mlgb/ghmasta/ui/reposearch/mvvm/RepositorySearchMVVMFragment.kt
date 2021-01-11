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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRepositorySearchBinding.inflate(inflater, container, false)
        binding.onClickListener = View.OnClickListener {
            binding.searchText.text?.let { searchText ->
                when (binding.radioName.isChecked) {
                    true -> viewModel.userIdLD
                    false -> viewModel.repositoryKeyword
                }.postValue(searchText.toString())
            }
        }

        binding.filter.setOnCheckedChangeListener { _, _ ->
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.clearDB()
            }
        }

        // The listener gets result form both paging source(CombinedLoadStates.source)
        // and remote mediator(CombinedLoadStates.mediator)
        // use refresh/append/prepend field to get combined results
        // can also access dedicated results from
        reposAdapter.addLoadStateListener { loadState ->
            checkLoadState(loadState.refresh, REFRESH, binding)
            checkLoadState(loadState.append, APPEND, binding)
            checkLoadState(loadState.prepend, PREPEND, binding)
        }

        binding.results.adapter = reposAdapter

        viewModel.reposResultLive.observe(viewLifecycleOwner) { reposList ->
            lifecycleScope.launch {
                reposAdapter.submitData(reposList)
            }
        }

        return binding.root
    }

    // only flip the UI components with type refresh, append and prepend UI errors will be handled
    // by header and footer
    private fun checkLoadState(
        state: LoadState,
        type: String,
        binding: FragmentRepositorySearchBinding
    ) {
        when (state) {
            is LoadState.NotLoading -> {
                repositoryLog("$type not loading")
                if (type == REFRESH) {
                    binding.results.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.retryButton.visibility = View.INVISIBLE
                }
            }
            LoadState.Loading -> {
                repositoryLog("$type refreshing")
                if (type == REFRESH) {
                    binding.results.visibility = View.INVISIBLE
                    binding.progressBar.visibility = View.VISIBLE
                    binding.retryButton.visibility = View.INVISIBLE
                }
            }
            is LoadState.Error -> {
                showError("Failed to load data for $type", state.error)
                if (type == REFRESH) {
                    binding.results.visibility = View.INVISIBLE
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.retryButton.visibility = View.VISIBLE
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