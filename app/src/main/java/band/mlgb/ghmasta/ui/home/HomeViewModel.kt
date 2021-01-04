package band.mlgb.ghmasta.ui.home

import androidx.lifecycle.*
import band.mlgb.ghmasta.data.model.Repository
import band.mlgb.ghmasta.network.GithubApi
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class HomeViewModel @Inject constructor(private val githubApi: GithubApi) : ViewModel() {

    val userIdLD: MutableLiveData<String> = MutableLiveData()

    val repos: LiveData<List<Repository>> = userIdLD.switchMap { userId ->
        liveData {
            emit(githubApi.fetchReposForUser(userId))
        }
    }

}