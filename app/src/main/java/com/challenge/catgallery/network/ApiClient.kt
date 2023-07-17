package com.challenge.catgallery.network

import android.content.Context
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

object ApiClient {
    private const val BASE_URL = "https://api.thecatapi.com/v1/"
    const val API_KEY = "live_eeET1X2tX6dsxGEUU8UCFbFZ97ZPZJv4HOod7layW6TpEKSVpZwJX3UFNjJOe9ZW "

    private lateinit var context: Context

    fun initialize(context: Context) {
        this.context = context
    }

    private val retrofit: Retrofit by lazy {
        val cacheDirectory = File(context.cacheDir, "api_cache")
        val cacheSize: Long = 10 * 1024 * 1024 // 10 MB
        val cache = Cache(cacheDirectory, cacheSize)

        val okHttpClient = OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor { chain ->
                val original: Request = chain.request()
                val requestBuilder: Request.Builder = original.newBuilder()
                    .header("x-api-key", API_KEY)
                val request: Request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val catApiService: CatApiService by lazy {
        retrofit.create(CatApiService::class.java)
    }
}
