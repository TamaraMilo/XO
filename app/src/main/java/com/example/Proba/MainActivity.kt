package com.example.Proba

import DifficultyScreen
import GameScreen
import WelcomeScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.Proba.States.Screen
import com.example.Proba.ui.theme.ProbaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                var screen by remember { mutableStateOf<Screen>(Screen.Welcome) }

                when (val s = screen) {
                    Screen.Welcome -> WelcomeScreen(
                        onStart = { screen = Screen.DifficultyPicker }
                    )

                    Screen.DifficultyPicker -> DifficultyScreen(
                        onPick = { diff -> screen = Screen.Game(diff) },
                        onBack = { screen = Screen.Welcome }
                    )

                    is Screen.Game -> GameScreen(
                        difficulty = s.difficulty,
                        onBack = { screen = Screen.DifficultyPicker }
                    )
                }
            }
        }
    }
}

