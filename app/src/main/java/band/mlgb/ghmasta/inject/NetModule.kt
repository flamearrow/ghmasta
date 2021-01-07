package band.mlgb.ghmasta.inject

import band.mlgb.ghmasta.network.GithubApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class NetModule {

    // check balance:
    // curl -H "Authorization: Bearer 1147bd6dcbf1eb6658ec115a0eb543d7c32e4d43" https://api.github.com/rate_limit
    @Provides
    @Singleton
    fun provideOKHttpClientWithAuthBearer(): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor { chain ->
            chain.proceed(
                chain.request().newBuilder().addHeader(
                    "Authorization",
                    "Bearer $ACCESS_TOKEN"
                ).build()
            )
        }.build()
    }

    @Provides
    @Singleton
    fun provideGithubApi(okHttpClient: OkHttpClient): GithubApi {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(GithubApi::class.java)
    }

    companion object {
        private const val ACCESS_TOKEN = "blahblah"
    }
}