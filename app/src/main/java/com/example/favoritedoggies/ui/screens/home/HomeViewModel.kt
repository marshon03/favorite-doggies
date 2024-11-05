package com.example.favoritedoggies.ui.screens.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.breed.BreedWithSubBreeds
import com.example.data.model.breed.getBreedsWithSubBreeds
import com.example.data.repository.DoggieRepository
import com.example.data.repository.FavoriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val doggieRepository: DoggieRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    val breedsList = mutableStateOf<List<BreedWithSubBreeds>?>(emptyList())
    val isLoading = mutableStateOf(false)
    val error = mutableStateOf("")

    init {
        fetchAllBreeds()
    }

    fun fetchAllBreeds() {
        viewModelScope.launch {
            isLoading.value = true
            val result = doggieRepository.fetchAllDogBreeds()

            result.onSuccess { breeds ->
                breedsList.value = getBreedsWithSubBreeds(breeds = breeds)

                viewModelScope.launch {
                    favoriteRepository.favoriteBreedsFlow.collect { favoriteBreeds ->
                        // Update breed list with favorite status
                        breedsList.value = breedsList.value?.map { breed ->
                            breed.copy(isFavorite = favoriteBreeds.contains(breed.breedName))
                        }
                    }
                }

                viewModelScope.launch {
                    favoriteRepository.favoriteSubBreedsFlow.collect { favoriteSubBreeds ->
                        val favoriteSubBreedsSet = favoriteSubBreeds.toSet()

                        breedsList.value = breedsList.value?.map { breed ->
                            // Only update sub-breeds if there’s a change in favorite status
                            val updatedSubBreeds = breed.subBreeds.map { subBreed ->
                                val isFavorite = favoriteSubBreedsSet.contains("${breed.breedName}-${subBreed.name}")
                                if (subBreed.isFavorite != isFavorite) {
                                    subBreed.copy(isFavorite = isFavorite)
                                } else {
                                    subBreed
                                }
                            }
                            breed.copy(subBreeds = updatedSubBreeds)
                        }
                    }
                }

                isLoading.value = false
            }.onFailure { e ->
                error.value = e.message ?: ""

                isLoading.value = false
            }
        }
    }

    fun onFavoriteClicked(breedName: String, subBreedName: String? = null) {
        val currentList = breedsList.value ?: return

        val updatedList = currentList.map { breedWithSubBreeds ->
            if (breedWithSubBreeds.breedName == breedName) {
                if (subBreedName != null) {
                    // Toggle the favorite status of the specific sub-breed
                    val updatedSubBreeds = breedWithSubBreeds.subBreeds.map { subBreed ->
                        if (subBreed.name == subBreedName) {
                            subBreed.copy(isFavorite = !subBreed.isFavorite)
                        } else subBreed
                    }
                    breedWithSubBreeds.copy(subBreeds = updatedSubBreeds)
                } else {
                    // Toggle the breed's favorite status and update all sub-breeds accordingly
                    val newFavoriteStatus = !breedWithSubBreeds.isFavorite
                    val updatedSubBreeds = breedWithSubBreeds.subBreeds.map { subBreed ->
                        subBreed.copy(isFavorite = newFavoriteStatus)
                    }
                    breedWithSubBreeds.copy(
                        isFavorite = newFavoriteStatus,
                        subBreeds = updatedSubBreeds
                    )
                }
            } else breedWithSubBreeds
        }

        breedsList.value = updatedList
        saveFavoritesToDataStore()
    }

    private fun saveFavoritesToDataStore() {
        viewModelScope.launch {
            val favoriteBreeds = breedsList.value?.filter { it.isFavorite }?.map { it.breedName } ?: emptyList()
            val favoriteSubBreeds = breedsList.value
                ?.asSequence()
                ?.filter { breed -> breed.subBreeds.any { it.isFavorite } }
                ?.flatMap { breed ->
                    breed.subBreeds.asSequence()
                        .filter { it.isFavorite }
                        .map { subBreed -> "${breed.breedName}-${subBreed.name}" }
                }
                ?.toList() ?: emptyList()

            favoriteRepository.saveFavoriteBreeds(favoriteBreeds, favoriteSubBreeds)
        }
    }
}