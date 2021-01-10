package band.mlgb.ghmasta.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import band.mlgb.ghmasta.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalPagingApi
@AndroidEntryPoint
class HomeFragment : Fragment() {


    @Inject
    lateinit var homeViewModel: HomeViewModel

    @Inject
    lateinit var reposAdapter: SearchResultPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.onClickListener = View.OnClickListener {
            binding.searchText.text?.let { searchText ->
                when (binding.radioName.isChecked) {
                    true -> homeViewModel.userIdLD
                    false -> homeViewModel.repositoryKeyword
                }.postValue(searchText.toString())
            }
        }

        binding.results.adapter = reposAdapter

        homeViewModel.reposResultLive.observe(viewLifecycleOwner) { reposList ->
            lifecycleScope.launch {
                reposAdapter.submitData(reposList)
            }
        }

        return binding.root
    }
}