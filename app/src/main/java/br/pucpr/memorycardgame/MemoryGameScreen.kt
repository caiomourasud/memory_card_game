package br.pucpr.memorycardgame

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.blur
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MemoryGameScreen(onExit: () -> Unit) {
    var cards by remember { mutableStateOf(generateCards()) }
    val flippedCards = remember { mutableStateListOf<MemoryCard>() }
    val scope = rememberCoroutineScope()
    val allMatched = remember { mutableStateOf(false) }
    var flippedCount by remember { mutableIntStateOf(0) }
    var rounds by remember { mutableIntStateOf(0) }
    val bestScores = remember { mutableStateListOf<Int>() }
    var showExitDialog by remember { mutableStateOf(false) }

    LaunchedEffect(cards) {
        allMatched.value = cards.all { it.isMatched }
        Log.d("MemoryGame", "allMatched: ${allMatched.value}")
        if (allMatched.value) {
            bestScores.add(rounds)
            Log.d("MemoryGame", "Melhores resultados: $bestScores")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.background_home),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(80.dp),
            contentScale = ContentScale.Crop
        )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).background(Color.Transparent),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Cards turned over: $flippedCount", color = Color.DarkGray)
                        Text(text = "Rounds: $rounds", color = Color.DarkGray)
                        Text(text = "Best Scores: ${bestScores.joinToString(", ")}", color = Color.DarkGray)
                        Spacer(modifier = Modifier.height(16.dp))
                        CardGrid(
                            cards = cards,
                            flippedCards = flippedCards,
                            onCardClicked = { card ->
                                handleCardClick(card, flippedCards, scope, allMatched, cards, bestScores, rounds) {
                                    flippedCount++
                                    if (flippedCards.size % 2 == 0) {
                                        rounds++
                                    }
                                }
                            }
                        )
                        if (allMatched.value) {
                            Column {
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = {
                                    scope.launch {
                                        resetAllCards(cards)
                                        resetFlippedCards(flippedCards)
                                        flippedCount = 0
                                        rounds = 0
                                        delay(300)
                                        flippedCards.clear()
                                        cards = generateCards()
                                        allMatched.value = false
                                    }
                                }) {
                                    Text("Restart game")
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { showExitDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
                            Text("Exit", color = Color.Black)
                        }
                    }
                }


    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Confirm Exit?") },
            text = { Text("When you exit the game you lose your score for the current game.") },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    onExit()
                }) {
                    Text("Exit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}