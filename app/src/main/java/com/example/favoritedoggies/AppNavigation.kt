package com.example.favoritedoggies

import androidx.activity.OnBackPressedDispatcher
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.favoritedoggies.ui.AppNavGraph

@Composable
fun AppNavigation(
    navController: NavHostController,
    onBackPressedDispatcher: OnBackPressedDispatcher,
    innerPadding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = Modifier.padding(innerPadding)
    ) {
        AppNavGraph(navController = navController, onBackPressedDispatcher = onBackPressedDispatcher)
    }
}