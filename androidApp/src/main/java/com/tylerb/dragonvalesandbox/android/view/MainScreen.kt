package com.tylerb.dragonvalesandbox.android.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.tylerb.dragonvalesandbox.api.DragonApi
import com.tylerb.dragonvalesandbox.breedCalc
import com.tylerb.dragonvalesandbox.model.DragonData
import com.tylerb.dragonvalesandbox.util.Result
import com.tylerb.dragonvalesandbox.util.myResultRunCatching


@Composable
fun MainScreen(modifier: Modifier = Modifier) {

    var dragonsReult: Result<List<DragonData>> by remember { mutableStateOf(Result.Loading) }

    LaunchedEffect(key1 = Unit) {
        dragonsReult = myResultRunCatching { DragonApi().getDragonList() }
    }

    dragonsReult.onSuccess {
        SandboxScreen(
            modifier = modifier,
            dragons = it
        )
    }




}