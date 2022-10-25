package com.tylerb.dragonvalesandbox.android.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tylerb.dragonvalesandbox.android.R
import coil.compose.AsyncImage
import com.tylerb.dragonvalesandbox.android.view.util.DragonDataPreview
import com.tylerb.dragonvalesandbox.model.DragonData

@Composable
fun AllDragonsScreen(
    modifier: Modifier = Modifier,
    dragons: List<DragonData>,
    showPercent: Boolean = true,
    header: @Composable () -> Unit
) {

    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Adaptive(minSize = 128.dp)
    ) {

        item(
            span = {
                GridItemSpan(maxLineSpan)
            }
        ) {
            header()
        }

        items(
            items = dragons,
            key = { it.name }
        ) { dragon ->
            Card(
                modifier = Modifier
                    .size(256.dp)
                    .padding(4.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        modifier = Modifier.size(80.dp),
                        model = "https://dvboxcdn.com/dragons/${dragon.image}",
                        placeholder = painterResource(id = R.drawable.placeholder_dragon),
                        contentDescription = null
                    )
                    AsyncImage(
                        modifier = Modifier.size(50.dp),
                        model = "https://dvboxcdn.com/dragons/${dragon.eggIcon}",
                        placeholder = painterResource(id = R.drawable.placeholder_egg),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.padding(top = 4.dp))
                    Text(
                        text = dragon.name,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "14:00:00",
                        fontSize = 16.sp
                    )
                    if (showPercent) {
                        Text(
                            text = "6.6%",
                            fontSize = 16.sp
                        )
                    }

                }
            }


        }


    }


}

@Composable
@Preview
fun AllDragonsScreenPreview() {

    val dragonPreview = (0..30).map { DragonDataPreview.blueFireDragon.copy(name = DragonDataPreview.blueFireDragon.name + it) }

    AllDragonsScreen(
        modifier = Modifier.fillMaxSize(),
        dragons = dragonPreview
    ) {

    }
}