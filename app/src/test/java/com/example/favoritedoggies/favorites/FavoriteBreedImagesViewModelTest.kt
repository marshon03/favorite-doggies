package com.example.favoritedoggies.favorites

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.data.repository.DoggieRepository
import com.example.data.repository.FavoriteRepository
import com.example.favoritedoggies.ui.screens.favoritebreedimages.FavoriteBreedImagesViewModel
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
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

@ExperimentalCoroutinesApi
class FavoriteBreedImagesViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val doggieRepository: DoggieRepository = mockk()
    private val favoriteRepository: FavoriteRepository = mockk()
    private lateinit var viewModel: FavoriteBreedImagesViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        coEvery { favoriteRepository.favoriteBreedsFlow } returns flowOf(listOf("australian"))
        coEvery { favoriteRepository.favoriteSubBreedsFlow } returns flowOf(listOf("australian-sub1", "australian-sub2"))

        viewModel = FavoriteBreedImagesViewModel(doggieRepository, favoriteRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test fetchFavoriteBreedsFromLocalStorage updates favoriteBreedsAndSubBreeds`() = runTest {
        // Mock the repositories
        coEvery { favoriteRepository.favoriteBreedsFlow } returns flowOf(listOf("australian"))
        coEvery { favoriteRepository.favoriteSubBreedsFlow } returns flowOf(listOf("australian-sub1", "australian-sub2"))
        coEvery { doggieRepository.fetchRandomBreedImages("australian") } returns Result.success("url_for_australian_image")

        viewModel.fetchFavoriteBreedsFromLocalStorage()
        advanceUntilIdle()

        val expectedImages = listOf("url_for_australian_image")
        assertEquals(expectedImages.first(), viewModel.favoriteBreedsAndSubBreeds.value.first())
    }

    @Test
    fun `test fetchFavoriteBreedsFromLocalStorage handles errors`() = runTest {
        // Mock the repositories
        coEvery { favoriteRepository.favoriteBreedsFlow } returns flowOf(listOf("australian"))
        coEvery { favoriteRepository.favoriteSubBreedsFlow } returns flowOf(listOf("australian-sub1", "australian-sub2"))

        coEvery { doggieRepository.fetchRandomBreedImages("australian") } returns Result.failure(Exception("Fetch error"))
        coEvery { doggieRepository.fetchRandomSubBreedImages("australian", "sub1") } returns Result.success("url_for_australian_sub1_image")

        viewModel.fetchFavoriteBreedsFromLocalStorage()
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
    }
}