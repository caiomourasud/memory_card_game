package br.pucpr.memorycardgame

import CreditsScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import br.pucpr.memorycardgame.ui.theme.MemoryCardGameTheme
import br.pucpr.memorycardgame.utils.ScoreManager
import br.pucpr.memorycardgame.views.MemoryGameScreen
import br.pucpr.memorycardgame.views.ScoresScreen
import br.pucpr.memorycardgame.views.StartScreen

class MainActivity : ComponentActivity() {
    private lateinit var scoreManager: ScoreManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scoreManager = ScoreManager(this)
        setContent {
            MemoryCardGameTheme {
                var currentScreen by remember { mutableStateOf("start") }

                when (currentScreen) {
                    "game" -> MemoryGameScreen(
                        scoreManager,
                        onExit = {
                            currentScreen = "start"
                        })
                    "credits" -> CreditsScreen(
                        onBack = {
                            currentScreen = "start"
                        })
                    "scores" -> ScoresScreen(
                        scoreManager,
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