package com.tylerb.dragonvalesandbox.android.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.tylerb.dragonvalesandbox.model.DragonData

@Composable
fun AllDragonsScreen(
    modifier: Modifier = Modifier,
    dragons: List<DragonData>
) {

    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Adaptive(minSize = 128.dp)
    ) {

        items(dragons) { dragon ->
            Card(
                modifier = Modifier
                    .size(128.dp)
                    .padding(4.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        modifier = Modifier.size(80.dp),
                        model = "https://dvboxcdn.com/dragons/${dragon.image}",
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.padding(top = 4.dp))
                    Text(
                        text = dragon.name,
                        fontSize = 16.sp
                    )
                }
            }


        }


    }


}