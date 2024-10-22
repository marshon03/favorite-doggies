package com.example.favoritedoggies.ui.screens.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.data.model.breed.BreedWithSubBreeds

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navigateToFavorites: () -> Unit, viewModel: HomeViewModel) {

    Scaffold(
        topBar = { TopAppBar(title = { Text("Favorite Doggies") }) },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(bottom = 24.dp, end = 24.dp),
                onClick = { navigateToFavorites() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Favorite, contentDescription = "Go to Favorite")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                viewModel.isLoading.value -> {
                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.align(Alignment.Center))
                }

                viewModel.error.value.isNotBlank() -> {
                    Log.d("ERROR", "Error: ${viewModel.error.value}")
                }

                else -> {
                    LazyColumn(contentPadding = PaddingValues(0.dp)) {
                        items(viewModel.breedsList.value ?: emptyList()) { breedItem ->
                            BreedItem(
                                breedItem,
                                onBreedFavoriteClick = { viewModel.onFavoriteClicked(breedItem.breedName) },
                                onSubBreedFavoriteClick = { subBreedName ->
                                    viewModel.onFavoriteClicked(breedItem.breedName, subBreedName)
                                })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BreedItem(
    breedWithSubBreeds: BreedWithSubBreeds,
    onBreedFavoriteClick: () -> Unit,
    onSubBreedFavoriteClick: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val asterisk = if (breedWithSubBreeds.subBreeds.isNotEmpty()) "*" else ""
            Text(text = breedWithSubBreeds.breedName + asterisk, modifier = Modifier.weight(1f))
            Icon(
                imageVector = if (breedWithSubBreeds.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (breedWithSubBreeds.isFavorite) "Unfavorite" else "Favorite",
                modifier = Modifier.clickable { onBreedFavoriteClick() },
                tint = if (breedWithSubBreeds.isFavorite) Color.Red else Color.Gray
            )
        }

        // Show sub-breeds if expanded
        if (isExpanded && breedWithSubBreeds.subBreeds.isNotEmpty()) {
            breedWithSubBreeds.subBreeds.forEach { subBreed ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable { onSubBreedFavoriteClick(subBreed.name) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = " - " + subBreed.name, modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = if (subBreed.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (subBreed.isFavorite) "Unfavorite" else "Favorite",
                        modifier = Modifier.clickable { onSubBreedFavoriteClick(subBreed.name) },
                        tint = if (subBreed.isFavorite) Color.Red else Color.Gray
                    )
                }
            }
        }
    }
}