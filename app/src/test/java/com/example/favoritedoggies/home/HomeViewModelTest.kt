package com.example.favoritedoggies.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.data.model.breed.BreedWithSubBreeds
import com.example.data.model.breed.SubBreed
import com.example.data.repository.DoggieRepository
import com.example.data.repository.FavoriteRepository
import com.example.favoritedoggies.ui.screens.home.HomeViewModel
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.coroutines.Continuation

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: HomeViewModel
    private val doggieRepository: DoggieRepository = mockk()
    private val favoriteRepository: FavoriteRepository = mockk()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        coEvery { doggieRepository.fetchAllDogBreeds() } returns Result.success(MockAllBreedsResponse.mockAllBreedsResponse().message)

        coEvery { favoriteRepository.favoriteBreedsFlow } returns flowOf(emptyList())
        coEvery { favoriteRepository.favoriteSubBreedsFlow } returns flowOf(emptyList())

        viewModel = HomeViewModel(doggieRepository, favoriteRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test fetchAllBreeds updates breedsList and isLoading states on success`() = runTest {
        // Mock the repositories
        coEvery { doggieRepository.fetchAllDogBreeds() } returns Result.success(MockAllBreedsResponse.mockAllBreedsResponse().message)

        viewModel.fetchAllBreeds()

        val subBreeds = viewModel.breedsList.value?.find { it.breedName == "Australian" }?.subBreeds
        assertEquals(mockBreedWithSubBreedsList().first().subBreeds.count(), subBreeds?.count())

        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `test fetchAllBreeds sets error state on failure`() = runTest {
        val errorMessage = "This is an error"

        // Mock the repositories
        coEvery { doggieRepository.fetchAllDogBreeds() } returns Result.failure(Exception(errorMessage))

        viewModel.fetchAllBreeds()

        advanceUntilIdle()

        assertEquals(errorMessage, viewModel.error.value)
        assertTrue(!viewModel.isLoading.value)
    }

    @Test
    fun `test onFavoriteClicked updates breed and sub-breed favorite status`() = runTest {
        // Mock the repositories
        coEvery { favoriteRepository.saveFavoriteBreeds(any(), any()) } just Runs

        viewModel.breedsList.value = mockBreedWithSubBreedsList()

        viewModel.onFavoriteClicked(breedName = "australian", subBreedName = "sub1")

        val updatedBreeds = viewModel.breedsList.value
        assertEquals(true, updatedBreeds?.first()?.subBreeds?.first()?.isFavorite)
        assertEquals(false, updatedBreeds?.first()?.isFavorite)
    }

    private fun mockBreedWithSubBreedsList(): List<BreedWithSubBreeds> {
        return listOf(
            BreedWithSubBreeds(
                breedName = "australian",
                subBreeds = listOf(
                    SubBreed(name = "sub1", isFavorite = false),
                    SubBreed(name = "sub2", isFavorite = false)
                ),
                isFavorite = false
            )
        )
    }
}