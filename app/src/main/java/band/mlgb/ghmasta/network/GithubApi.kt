package band.mlgb.ghmasta.network

import band.mlgb.ghmasta.data.model.Repository
import band.mlgb.ghmasta.data.model.RepositorySearchResponse
import band.mlgb.ghmasta.data.model.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApi {
    @GET("users/{user_id}")
    suspend fun fetchUser(@Path("user_id") userId: String): User

    @GET("users/{user_id}/repos")
    suspend fun fetchReposForUser(@Path("user_id") userId: String): List<Repository>

    @GET("users/{user_id}/repos")
    suspend fun fetchReposForUserResponse(@Path("user_id") userId: String): Response<List<Repository>>

    @GET("users/{user_id}/repos")
    suspend fun fetchReposForUserResponse(
        @Path("user_id") userId: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = REPOSITORIES_PER_PAGE
    ): Response<List<Repository>>


    @GET("search/repositories?sort=stars")
    suspend fun searchRepoWithKeyword(
        @Query("q") keyword: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = REPOSITORIES_PER_PAGE
    ): Response<RepositorySearchResponse>

    companion object {
        private const val REPOSITORIES_PER_PAGE = 20
    }
}