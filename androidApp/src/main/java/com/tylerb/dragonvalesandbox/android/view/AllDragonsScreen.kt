package com.tylerb.dragonvalesandbox.android.view

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.palette.graphics.Palette
import coil.compose.AsyncImage
import com.tylerb.dragonvalesandbox.model.DragonData


fun calcDominantColor(drawable: Drawable, onFinish: (Color) -> Unit) {
    val bmp = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)

    Palette.from(bmp).generate { palette ->
        palette?.dominantSwatch?.rgb?.let { colorValue ->
            onFinish(Color(colorValue))
        }
    }

}

@Composable
fun AllDragonsScreen(
    modifier: Modifier = Modifier,
    dragons: List<DragonData>,
    showPercent: Boolean = true,
    header: @Composable () -> Unit
) {

    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Adaptive(minSize = 136.dp)
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

            val defaultColor = MaterialTheme.colorScheme.surface

            var cardColor by remember { mutableStateOf(defaultColor) }

            Card(
                colors = CardDefaults.cardColors(),
                modifier = Modifier.size (264.dp).padding(4.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    cardColor,
                                    defaultColor
                                )
                            )
                        )
                    ,
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        modifier = Modifier.size(80.dp),
                        model = dragon.imageUrl,
                        contentDescription = null,
                        onSuccess = {
                            calcDominantColor(it.result.drawable) { color ->
                                cardColor = color
                            }

                        }
                    )
                    AsyncImage(
                        modifier = Modifier.size(50.dp),
                        model = dragon.eggIconUrl,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.padding(top = 4.dp))
                    Row {
                       dragon.flagImageUrls.forEach { flagUrl ->
                           AsyncImage(
                               modifier = Modifier.size(24.dp),
                               model = flagUrl,
                               contentDescription = null
                           )
                       }
                    }
                    Spacer(modifier = Modifier.padding(top = 4.dp))
                    Text(
                        text = dragon.name,
                        fontSize = 16.sp
                    )
                    Text(
                        text = dragon.dhms,
                        fontSize = 16.sp
                    )
                    if (showPercent) {
                        Text(
                            text = "${dragon.percent}%",
                            fontSize = 16.sp
                        )
                    }

                }
            }


        }


    }


}