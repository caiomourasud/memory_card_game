package br.pucpr.memorycardgame

import CreditsScreen
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
                var currentScreen by remember { mutableStateOf("start") }

                when (currentScreen) {
                    "game" -> MemoryGameScreen(
                        onExit = {
                            currentScreen = "start"
                        })
                    "credits" -> CreditsScreen(
                        onBack = {
                            currentScreen = "start"
                        })
                    "scores" -> ScoresScreen(
                        onBack = {
                            currentScreen = "start"
                        })
                    else -> StartScreen(
                        onStartClick = {
                            currentScreen = "game"
                        },
                        onCreditsClick = {
                            currentScreen = "credits"
                        },
                        onScoresClick = {
                            currentScreen = "scores"
                        },
                    )
                }
            }
        }
    }
}