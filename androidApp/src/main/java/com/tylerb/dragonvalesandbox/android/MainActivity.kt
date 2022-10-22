package com.tylerb.dragonvalesandbox.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.tylerb.dragonvalesandbox.android.ui.theme.DragonTheme
import com.tylerb.dragonvalesandbox.android.view.MainScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DragonTheme {
                MainScreen(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
