package com.challenge.catgallery.viewmodel

import android.nfc.tech.MifareUltralight.PAGE_SIZE
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.challenge.catgallery.model.Breed
import com.challenge.catgallery.model.Cat
import com.challenge.catgallery.network.ApiClient
import com.challenge.catgallery.network.CatApiService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CatViewModel : ViewModel() {
    private val catApiService: CatApiService = ApiClient.catApiService

    private val _cats = MutableLiveData<List<Cat>?>()
    val cats: MutableLiveData<List<Cat>?> = _cats

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _isLastPage = MutableLiveData<Boolean>()
    val isLastPage: LiveData<Boolean> = _isLastPage

    private var currentPage = 1
    private var totalPage = 10

    private var currentBreed: String = ""

    fun searchCatsByBreed(breed: String, limit: Int, page: Int, callback: () -> Unit) {
        currentBreed = breed
        _isLoading.value = true

        catApiService.searchBreeds(ApiClient.API_KEY).enqueue(object : Callback<List<Breed>> {
            override fun onResponse(call: Call<List<Breed>>, response: Response<List<Breed>>) {
                if (response.isSuccessful) {
                    val breeds = response.body()
                    val breedId = getBreedId(breeds, breed)
                    if (breedId != null) {
                        catApiService.searchImagesByBreed(ApiClient.API_KEY, breedId, limit, page)
                            .enqueue(object : Callback<List<Cat>> {
                                override fun onResponse(
                                    call: Call<List<Cat>>,
                                    response: Response<List<Cat>>,
                                ) {
                                    if (response.isSuccessful) {
                                        val cats = response.body()
                                        if (page == 1) {
                                            _cats.value = cats
                                        } else {
                                            val currentCats = _cats.value.orEmpty().toMutableList()
                                            currentCats.addAll(cats.orEmpty())
                                            _cats.value = currentCats
                                        }
                                        _error.value = null

                                        // Actualiza la página
                                        currentPage = page

                                        // Calcula el número total de páginas
                                        totalPage = (cats?.size ?: 10).div(PAGE_SIZE) + 1
                                    } else {
                                        _error.value = "Error: ${response.code()}"
                                    }
                                    _isLoading.value = false
                                    callback()
                                }

                                override fun onFailure(call: Call<List<Cat>>, t: Throwable) {
                                    _error.value = "Error: ${t.message}"
                                    _isLoading.value = false
                                    callback()
                                }
                            })
                    } else {
                        _error.value = "Breed not found."
                        _isLoading.value = false
                        callback()
                    }
                } else {
                    _error.value = "Error: ${response.code()}"
                    _isLoading.value = false
                    callback()
                }
            }

            override fun onFailure(call: Call<List<Breed>>, t: Throwable) {
                _error.value = "Error: ${t.message}"
                _isLoading.value = false
                callback()
            }
        })
    }

    fun loadMoreCatsIfNeeded(visibleItemCount: Int, lastVisibleItemPosition: Int) {
        if (!isLoading.value?.not()!! && currentPage < totalPage &&
            visibleItemCount + lastVisibleItemPosition >= (cats.value?.size ?: 10) - 5
        ) {
            loadMoreCats()
        }
    }


    private fun loadMoreCats() {
        if (isLoading.value == false && currentPage < totalPage) {
            _isLoading.value = true
            catApiService.searchBreeds(ApiClient.API_KEY).enqueue(object : Callback<List<Breed>> {
                override fun onResponse(call: Call<List<Breed>>, response: Response<List<Breed>>) {
                    if (response.isSuccessful) {
                        val breeds = response.body()
                        val breedId = getBreedId(breeds, currentBreed)

                        if (breedId != null) {
                            catApiService.searchImagesByBreed(
                                ApiClient.API_KEY,
                                breedId,
                                PAGE_SIZE,
                                currentPage + 1
                            )
                                .enqueue(object : Callback<List<Cat>> {
                                    override fun onResponse(
                                        call: Call<List<Cat>>,
                                        response: Response<List<Cat>>,
                                    ) {
                                        if (response.isSuccessful) {
                                            val cats = response.body()
                                            val currentCats = _cats.value.orEmpty().toMutableList()
                                            currentCats.addAll(cats.orEmpty())
                                            _cats.value = currentCats
                                            _error.value = null

                                            // Actualiza la página
                                            currentPage = currentPage + 1

                                            // Calcula el número total de páginas
                                            totalPage = (cats?.size ?: 10).div(PAGE_SIZE) + 1
                                        } else {
                                            _error.value = "Error: ${response.code()}"
                                        }
                                        _isLoading.value = false
                                    }

                                    override fun onFailure(call: Call<List<Cat>>, t: Throwable) {
                                        _error.value = "Error: ${t.message}"
                                        _isLoading.value = false
                                    }
                                })
                        } else {
                            _error.value = "Breed not found."
                            _isLoading.value = false
                        }
                    } else {
                        _error.value = "Error: ${response.code()}"
                        _isLoading.value = false
                    }
                }

                override fun onFailure(call: Call<List<Breed>>, t: Throwable) {
                    _error.value = "Error: ${t.message}"
                    _isLoading.value = false
                }
            })
        }
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
