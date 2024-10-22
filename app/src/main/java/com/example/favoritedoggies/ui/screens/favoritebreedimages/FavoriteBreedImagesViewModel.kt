package com.example.favoritedoggies.ui.screens.favoritebreedimages

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.DoggieRepository
import com.example.data.repository.FavoriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteBreedImagesViewModel @Inject constructor(
    private val doggieRepository: DoggieRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    val favoriteBreedsAndSubBreeds = mutableStateOf<List<String>>(emptyList())
    val isLoading = mutableStateOf(false)
    val error = mutableStateOf("")

    init {
        fetchFavoriteBreedsFromLocalStorage()
    }

    fun fetchFavoriteBreedsFromLocalStorage() {
        viewModelScope.launch {
            val breeds = arrayListOf<String>()

            favoriteRepository.favoriteBreedsFlow.collect { favoriteBreeds ->
                favoriteRepository.favoriteSubBreedsFlow.collect { favoriteSubBreeds ->

                    val validFavoriteBreeds = favoriteBreeds.filter { it.isNotBlank() }

                    if (validFavoriteBreeds.isNotEmpty()) {
                        // Associate breeds with their favorite sub-breeds
                        val breedToFavoriteSubBreedsMap = validFavoriteBreeds.associateWith { breed ->
                            favoriteSubBreeds.filter { subBreed ->
                                subBreed.startsWith(breed, ignoreCase = true)
                            }
                        }

                        breedToFavoriteSubBreedsMap.forEach { (breed, subBreeds) ->
                            breeds.add(breed)

                            if (!validFavoriteBreeds.contains(breed)) {
                                breeds.addAll(subBreeds)
                            }
                        }
                    }

                    val independentSubBreeds = if (validFavoriteBreeds.isEmpty()) {
                        favoriteSubBreeds
                    } else {
                        favoriteSubBreeds.filter { subBreed ->
                            validFavoriteBreeds.none { breed ->
                                subBreed.startsWith(breed, ignoreCase = true)
                            }
                        }
                    }

                    // Add favorited sub-breeds without parent breed
                    if (independentSubBreeds.isNotEmpty()) {
                        breeds.addAll(independentSubBreeds)
                    }

                    fetchBreedImages(breedTypes = breeds)
                }
            }
        }
    }

    private fun fetchBreedImages(breedTypes: List<String>) {
        viewModelScope.launch {
            isLoading.value = true

            // Use async to run both fetch calls concurrently
            val breedImagesDeferred = breedTypes.mapNotNull { breedType ->
                val parts = breedType.split("-")
                if (parts.size == 2) {
                    null
                } else {
                    async { doggieRepository.fetchRandomBreedImages(breedType.lowercase()) }
                }
            }

            val subBreedImagesDeferred = breedTypes.mapNotNull { breedType ->
                val parts = breedType.split("-")
                if (parts.size == 2) {
                    val mainBreed = parts[0]
                    val subBreed = parts[1]

                    async { doggieRepository.fetchRandomSubBreedImages(mainBreed.lowercase(), subBreed) }
                } else {
                    null
                }
            }

            val breedImagesResults = breedImagesDeferred.awaitAll()
            val subBreedImagesResults = subBreedImagesDeferred.awaitAll()

            processResults(breedImagesResults)
            processResults(subBreedImagesResults)
        }
    }

    private fun processResults(results: List<Result<String>>) {
        results.forEach { result ->
            result.fold(
                onSuccess = { imageUrl ->
                    favoriteBreedsAndSubBreeds.value += imageUrl

                    isLoading.value = false
                },
                onFailure = { exception ->
                    isLoading.value = false
                }
            )
        }
    }
}