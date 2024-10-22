package com.example.data.api

import com.example.data.model.breed.AllBreedsResponse
import com.example.data.model.breed.RandomBreedImagesResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface DoggieApi {
    @GET("breeds/list/all")
    suspend fun getAllBreeds(): AllBreedsResponse

    @GET("breed/{breedType}/images/random")
    suspend fun getRandomBreedImages(@Path("breedType") breedType: String): RandomBreedImagesResponse

    @GET("breed/{breedType}/{subBreedType}/images/random")
    suspend fun getRandomSubBreedImages(
        @Path("breedType") breedType: String,
        @Path("subBreedType") subBreedType: String
    ): RandomBreedImagesResponse
}