package com.challenge.catgallery.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.challenge.catgallery.model.Breed
import com.challenge.catgallery.model.Cat
import com.challenge.catgallery.network.ApiClient
import com.challenge.catgallery.network.CatApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CatViewModel : ViewModel() {
    private val catApiService: CatApiService = ApiClient.catApiService

    private val _cats = MutableLiveData<List<Cat>>()
    val cats: LiveData<List<Cat>> = _cats

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun searchCatsByBreed(breed: String, limit: Int) {
        _isLoading.value = true

        catApiService.searchBreeds(ApiClient.API_KEY).enqueue(object : Callback<List<Breed>> {
            override fun onResponse(call: Call<List<Breed>>, response: Response<List<Breed>>) {
                if (response.isSuccessful) {
                    val breeds = response.body()
                    val breedId = getBreedId(breeds, breed)
                    if (breedId != null) {
                        catApiService.searchImagesByBreed(ApiClient.API_KEY, breedId, limit)
                            .enqueue(object : Callback<List<Cat>> {
                                override fun onResponse(
                                    call: Call<List<Cat>>,
                                    response: Response<List<Cat>>,
                                ) {
                                    if (response.isSuccessful) {
                                        val cats = response.body()
                                        _cats.value = cats
                                        _error.value = null
                                    } else {
                                        _cats.value = emptyList()
                                        _error.value = "Error: ${response.code()}"
                                    }
                                    _isLoading.value = false
                                }

                                override fun onFailure(call: Call<List<Cat>>, t: Throwable) {
                                    _cats.value = emptyList()
                                    _error.value = "Error: ${t.message}"
                                    _isLoading.value = false
                                }
                            })
                    } else {
                        _cats.value = emptyList()
                        _error.value = "Breed not found."
                        _isLoading.value = false
                    }
                } else {
                    _cats.value = emptyList()
                    _error.value = "Error: ${response.code()}"
                    _isLoading.value = false
                }
            }

            override fun onFailure(call: Call<List<Breed>>, t: Throwable) {
                _cats.value = emptyList()
                _error.value = "Error: ${t.message}"
                _isLoading.value = false
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
}
