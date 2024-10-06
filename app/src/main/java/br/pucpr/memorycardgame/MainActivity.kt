package br.pucpr.memorycardgame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import br.pucpr.memorycardgame.ui.theme.MemoryCardGameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MemoryCardGameTheme {
                var startGame by remember { mutableStateOf(false) }

                if (startGame) {
                    MemoryGameScreen(onExit = { startGame = false })
                } else {
                    StartScreen { startGame = true }
                }
            }
        }
    }
}