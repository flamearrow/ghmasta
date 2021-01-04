package band.mlgb.ghmasta.network

import band.mlgb.ghmasta.data.model.Repository
import band.mlgb.ghmasta.data.model.User
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubApi {
    @GET("users/{user_id}")
    suspend fun fetchUser(@Path("user_id") userId: String): User

    @GET("users/{user_id}/repos")
    suspend fun fetchReposForUser(@Path("user_id") userId: String): List<Repository>
}