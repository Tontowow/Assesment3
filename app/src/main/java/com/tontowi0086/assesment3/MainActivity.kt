package com.tontowi0086.assesment3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tontowi0086.assesment3.ui.screen.MainScreen
import com.tontowi0086.assesment3.ui.theme.Assesment3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assesment3Theme {
                MainScreen()
            }
        }
    }
}