package com.challenge.catgallery.network

import com.challenge.catgallery.model.Breed
import com.challenge.catgallery.model.Cat
import com.challenge.catgallery.network.ApiClient.catApiService

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun searchImagesByBreed(breedName: String, callback: (List<Cat>?, String?) -> Unit) {
    // Primera llamada para obtener la lista de razas
    catApiService.searchBreeds(ApiClient.API_KEY)
        .enqueue(object : Callback<List<Breed>> {
            override fun onResponse(call: Call<List<Breed>>, response: Response<List<Breed>>) {
                if (response.isSuccessful) {
                    val breeds = response.body()
                    val breedId = getBreedId(breeds, breedName)
                    if (breedId != null) {
                        // Segunda llamada para obtener las imágenes de gatos por raza
                        catApiService.searchImagesByBreed(ApiClient.API_KEY, breedId, 100, 1)
                            .enqueue(object : Callback<List<Cat>> {
                                override fun onResponse(
                                    call: Call<List<Cat>>,
                                    response: Response<List<Cat>>,
                                ) {
                                    if (response.isSuccessful) {
                                        val cats = response.body()
                                        callback(cats, null)
                                    } else {
                                        callback(null, "Error: ${response.code()}")
                                    }
                                }

                                override fun onFailure(call: Call<List<Cat>>, t: Throwable) {
                                    callback(null, "Error: ${t.message}")
                                }
                            })
                    } else {
                        callback(null, "Raza no encontrada.")
                    }
                } else {
                    callback(null, "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Breed>>, t: Throwable) {
                callback(null, "Error: ${t.message}")
            }
        })
}

private fun getBreedId(breeds: List<Breed>?, breedName: String): String? {
    breeds?.forEach { breed ->
        if (breed.name.equals(breedName, ignoreCase = true)) {
            return breed.id
        }
    }
    return null
}

