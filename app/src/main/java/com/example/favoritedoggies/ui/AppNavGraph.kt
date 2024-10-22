package com.example.favoritedoggies.ui

import androidx.activity.OnBackPressedDispatcher
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.favoritedoggies.ui.screens.favoritebreedimages.FavoriteBreedImagesScreen
import com.example.favoritedoggies.ui.screens.home.HomeScreen

fun NavGraphBuilder.AppNavGraph(
    navController: NavController, onBackPressedDispatcher: OnBackPressedDispatcher
) {
    navigation(
        route = "home",
        startDestination = "home_start_destination"
    ) {
        composable("home_start_destination") {
            HomeScreen({ navController.navigate("favorites_destination") }, hiltViewModel())
        }

        composable("favorites_destination") {
            FavoriteBreedImagesScreen(hiltViewModel())
        }
    }
}