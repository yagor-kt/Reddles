package com.example.riddles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.riddles.ui.navigation.NavGraph
import com.example.riddles.ui.theme.RiddlesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RiddlesTheme {
                NavGraph()
            }
        }
    }
}