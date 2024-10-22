package com.example.data.repository

import com.example.data.api.DoggieApi
import com.example.data.model.breed.Breeds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DoggieRepository @Inject constructor(private val doggieApi: DoggieApi) {

    suspend fun fetchAllDogBreeds(): Result<Breeds> {
        return try {
            withContext(Dispatchers.IO) {
                val response = doggieApi.getAllBreeds()

                Result.success(response.message)
            }
        } catch (e: HttpException) {
            Result.failure(e)
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    suspend fun fetchRandomBreedImages(breedType: String): Result<String> {
        return try {
            withContext(Dispatchers.IO) {
                val response = doggieApi.getRandomBreedImages(breedType)

                Result.success(response.message)
            }
        } catch (e: HttpException) {
            Result.failure(e)
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    suspend fun fetchRandomSubBreedImages(
        breedType: String,
        subBreedType: String
    ): Result<String> {
        return try {
            withContext(Dispatchers.IO) {
                val response = doggieApi.getRandomSubBreedImages(breedType, subBreedType)

                Result.success(response.message)
            }
        } catch (e: HttpException) {
            Result.failure(e)
        } catch (e: IOException) {
            Result.failure(e)
        }
    }
}