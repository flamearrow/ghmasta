package band.mlgb.ghmasta.ui.repomvvm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import band.mlgb.ghmasta.databinding.FragmentRepositorySearchBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * View for Repository search MVVM
 */
@ExperimentalPagingApi
@AndroidEntryPoint
class RepositorySearchMVVMFragment : Fragment() {

    @Inject
    lateinit var repositorySearchMVVMViewModel: RepositorySearchMVVMViewModel

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
                    true -> repositorySearchMVVMViewModel.userIdLD
                    false -> repositorySearchMVVMViewModel.repositoryKeyword
                }.postValue(searchText.toString())
            }
        }

        binding.results.adapter = reposAdapter

        repositorySearchMVVMViewModel.reposResultLive.observe(viewLifecycleOwner) { reposList ->
            lifecycleScope.launch {
                reposAdapter.submitData(reposList)
            }
        }

        return binding.root
    }

}