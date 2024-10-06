package br.pucpr.memorycardgame

import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.blur
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.DialogProperties

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

    val context = LocalContext.current
    val soundPool =
        SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(
                AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build())
            .build()

    val matchSoundId = soundPool.load(context, R.raw.match_sound, 1)


    LaunchedEffect(cards) {
        allMatched.value = cards.all { it.isMatched }
        Log.d("MemoryGame", "allMatched: ${allMatched.value}")
        if (allMatched.value) {
            bestScores.add(rounds)
            Log.d("MemoryGame", "Best Scores: $bestScores")
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
                        handleCardClick(card, flippedCards, scope, allMatched, cards, bestScores, rounds, incrementFlippedCount = { flippedCount++
                            if (flippedCards.size % 2 == 0) {
                                rounds++
                            } }, onCardsMatched = {
                            soundPool.play(matchSoundId, 0.5f, 0.5f, 0, 0, 2f)

                        })
                    }
                )
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

    if (allMatched.value) {
        AlertDialog(
            onDismissRequest = { allMatched.value = false },
            properties = DialogProperties(dismissOnClickOutside = false),
            title = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("YOUR SCORE", textAlign = TextAlign.Center)
                }

            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "$rounds", style = MaterialTheme.typography.displayMedium, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { onExit() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Home")
                        }
                        IconButton(onClick = {
                            resetAllCards(cards)
                            resetFlippedCards(flippedCards)
                            flippedCount = 0
                            rounds = 0
                            cards = generateCards()
                            allMatched.value = false
                        }) {
                            Icon(imageVector = Icons.Filled.RestartAlt, contentDescription = "Restart")
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}