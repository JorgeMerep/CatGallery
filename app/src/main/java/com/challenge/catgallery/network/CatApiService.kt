package com.challenge.catgallery.network

import com.challenge.catgallery.model.Breed
import com.challenge.catgallery.model.Cat
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CatApiService {
    @GET("images/search")
    fun searchImagesByBreed(
        @Query("api_key") apiKey: String,
        @Query("breed_ids") breed: String,
        @Query("limit") limit: Int,
    ): Call<List<Cat>>

    @GET("breeds")
    fun searchBreeds(
        @Query("api_key") apiKey: String,
    ): Call<List<Breed>>
}
