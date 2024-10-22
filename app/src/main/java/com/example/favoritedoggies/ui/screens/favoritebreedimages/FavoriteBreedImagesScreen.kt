package com.example.favoritedoggies.ui.screens.favoritebreedimages

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteBreedImagesScreen(viewModel: FavoriteBreedImagesViewModel) {

    val images = remember { viewModel.favoriteBreedsAndSubBreeds }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Favorite Doggies") }) }
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
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Color.White),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        HorizontalUncontainedCarousel(
                            state = rememberCarouselState {
                                images.value.count()
                            },
                            itemWidth = 250.dp,
                            itemSpacing = 12.dp,
                            contentPadding = PaddingValues(start = 12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 12.dp, bottom = 12.dp)
                        ) { index ->
                            val value = images.value[index]

                            Image(
                                painter = rememberAsyncImagePainter(model = value),
                                contentDescription = "",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .height(350.dp)
                                    .fillMaxWidth()
                                    .maskClip(shape = MaterialTheme.shapes.extraLarge)
                            )
                        }
                    }
                }
            }
        }
    }
}