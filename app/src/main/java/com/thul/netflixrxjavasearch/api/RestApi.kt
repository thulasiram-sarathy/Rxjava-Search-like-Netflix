package com.an.github.rest

import com.thul.netflixrxjavasearch.AppConstants.Companion.BASE_URL
import com.thul.netflixrxjavasearch.response.SearchResponse
import io.reactivex.Single
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface RestApi {

    @GET("search/movie")
    fun getMoviesList(@Query("query") query: String?,@Query("api_key") api_key:String, @Query("page") page: Int?
    ): Single<SearchResponse>

    companion object {

        fun create(httpUrl: HttpUrl): RestApi {
            val okHttpClient = OkHttpClient.Builder()
                .build()
            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
                .create(RestApi::class.java)
        }


        fun create(): RestApi = create(HttpUrl.parse(BASE_URL)!!)
    }

}