package br.pucpr.memorycardgame

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun CardGrid(
    cards: List<MemoryCard>,
    flippedCards: List<MemoryCard>,
    onCardClicked: (MemoryCard) -> Unit
) {
    val gridSize = 4
    val animatedPositions = remember { mutableStateListOf<Pair<Float, Float>>() }

    LaunchedEffect(cards) {
        if (cards.isNotEmpty()) {
            val initialOffset = Pair(0f, 0f)
            val targetPositions = cards.mapIndexed { index, _ ->
                val row = index / gridSize
                val column = index % gridSize
                Pair((column * 90).toFloat(), (row * 90).toFloat())
            }

            animatedPositions.clear()
            animatedPositions.addAll(List(cards.size) { initialOffset })

            for (i in cards.indices) {
                delay(100)
                animatedPositions[i] = targetPositions[i]
            }
        }
    }

    Box(modifier = Modifier.fillMaxWidth().height(380.dp)) {
        for (index in cards.indices) {
            val card = cards[index]
            val isFlipped = flippedCards.contains(card)

            val animatedOffset = animatedPositions.getOrElse(index) { Pair(0f, 0f) }
            val animatedX = animateFloatAsState(targetValue = animatedOffset.first, label = "")
            val animatedY = animateFloatAsState(targetValue = animatedOffset.second, label = "")

            val isMoving = animatedOffset != Pair(0f, 0f)

            CardItem(
                card = card.copy(isFlipped = isFlipped),
                onClick = { onCardClicked(card) },
                modifier = Modifier
                    .offset(x = animatedX.value.dp, y = animatedY.value.dp)
                    .size(90.dp),
                isMoving || index == 0
            )
        }
    }
}