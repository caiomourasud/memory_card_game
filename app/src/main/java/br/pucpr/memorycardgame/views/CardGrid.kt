package br.pucpr.memorycardgame.views

import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import br.pucpr.memorycardgame.models.MemoryCard
import br.pucpr.memorycardgame.R
import kotlinx.coroutines.delay

@Composable
fun CardGrid(
    cards: List<MemoryCard>,
    flippedCards: List<MemoryCard>,
    onCardClicked: (MemoryCard) -> Unit
) {
    val gridSize = 4
    val animatedPositions = remember { mutableStateListOf<Pair<Float, Float>>() }
    val cardSize = 90.dp

    val context = LocalContext.current
    val soundPool =
        SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build())
            .build()

    val cardSoundId = soundPool.load(context, R.raw.card_sound, 1)


    LaunchedEffect(cards) {
        if (cards.isNotEmpty()) {
            val initialOffset = Pair(0f, 0f)
            val targetPositions = List(cards.size) { index ->
                val row = index / gridSize
                val column = index % gridSize
                Pair((column * 90).toFloat(), (row * 90).toFloat())
            }

            animatedPositions.clear()
            animatedPositions.addAll(List(cards.size) { initialOffset })

            for (i in cards.indices) {
                delay(100)
                animatedPositions[i] = targetPositions[i]
                soundPool.play(cardSoundId, 0.5f, 0.5f, 0, 0, 2f)
            }
        }
    }

    Box(modifier = Modifier.fillMaxWidth().height(380.dp)) {
        val cardWidth = LocalConfiguration.current.screenWidthDp.dp / gridSize

        for (index in cards.indices) {
            val card = cards[index]
            val isFlipped = flippedCards.contains(card)

            val animatedOffset = animatedPositions.getOrElse(index) { Pair(0f, 0f) }
            val animatedX = animateFloatAsState(targetValue = animatedOffset.first, label = "")
            val animatedY = animateFloatAsState(targetValue = animatedOffset.second, label = "")

            val isMoving = animatedOffset != Pair(0f, 0f)

            CardItem(
                card = card.copy(isFlipped = isFlipped),
                onClick = {
                    onCardClicked(card)
                    if(!isFlipped && !card.isMatched) {
                        soundPool.play(cardSoundId, 0.5f, 0.5f, 0, 0, 2f)
                    }
                },
                modifier = Modifier
                    .size(cardWidth, cardWidth)
                    .padding(4.dp)
                    .offset(x = animatedX.value.dp, y = animatedY.value.dp),
                isMoving || index == 0
            )
        }
    }
}