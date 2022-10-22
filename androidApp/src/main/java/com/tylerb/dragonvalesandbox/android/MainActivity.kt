package com.tylerb.dragonvalesandbox.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tylerb.dragonvalesandbox.Greeting
import android.widget.TextView
import com.tylerb.dragonvalesandbox.api.DragonApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun greet(): String {
    return Greeting().greeting()
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GlobalScope.launch {
            DragonApi().getDragonList()
        }


        val tv: TextView = findViewById(R.id.text_view)
        tv.text = greet()
    }
}
