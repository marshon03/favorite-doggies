package com.example.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore by preferencesDataStore(name = "favorites_datastore")

@Singleton
class FavoriteRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private val FAVORITE_BREEDS_KEY = stringPreferencesKey("favorite_breeds")
    private val FAVORITE_SUBBREEDS_KEY = stringPreferencesKey("favorite_subbreeds")

    val favoriteBreedsFlow: Flow<List<String>> = context.dataStore.data.map { preferences ->
        preferences[FAVORITE_BREEDS_KEY]?.split(",") ?: emptyList()
    }

    val favoriteSubBreedsFlow: Flow<List<String>> = context.dataStore.data.map { preferences ->
        preferences[FAVORITE_SUBBREEDS_KEY]?.split(",") ?: emptyList()
    }

    suspend fun saveFavoriteBreeds(favoriteBreeds: List<String>, favoriteSubBreeds: List<String>) {
        context.dataStore.edit { preferences ->
            preferences[FAVORITE_BREEDS_KEY] = favoriteBreeds.joinToString(separator = ",")
            preferences[FAVORITE_SUBBREEDS_KEY] = favoriteSubBreeds.joinToString(separator = ",")
        }
    }
}