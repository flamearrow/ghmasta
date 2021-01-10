package band.mlgb.ghmasta.ui.repomvi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import band.mlgb.ghmasta.data.RepositoryState
import band.mlgb.ghmasta.databinding.FragmentRepositorySearchBinding
import band.mlgb.ghmasta.ui.repomvvm.SearchResultPagingAdapter
import band.mlgb.ghmasta.ui.views.RepositorySearchView
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRepositorySearchBinding.inflate(inflater, container, false)
        binding.onClickListener = View.OnClickListener {
            binding.searchText.text?.let { searchText ->
                when (binding.radioName.isChecked) {
                    true -> userName
                    false -> repositoryKeyword
                }.postValue(searchText.toString())
            }
        }

        binding.results.adapter = reposAdapter

        repositorySearchMVVMViewModel.bind(this, this)

        return binding.root
    }

    private val userName = MutableLiveData<String>()
    private val repositoryKeyword = MutableLiveData<String>()
    private val clearResult = MutableLiveData<Unit>()

    override fun renderer(state: RepositoryState) {
        when (state) {
            is RepositoryState.DataState -> {
                lifecycleScope.launch {
                    reposAdapter.submitData(state.data)
                }
            }
            is RepositoryState.ErrorState -> {
                TODO()
            }
            RepositoryState.LoadingState -> {
                TODO()
            }

        }
    }

    override fun searchWithUserNameIntent(): LiveData<String> = userName

    override fun searchWithKeywordIntent(): LiveData<String> = repositoryKeyword

    override fun clearResultIntent(): LiveData<Unit> = clearResult
}