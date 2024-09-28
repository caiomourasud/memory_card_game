package br.pucpr.memorycardgame

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import br.pucpr.memorycardgame.ui.theme.MemoryCardGameTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MemoryCardGameTheme {
                MemoryGameScreen()
            }
        }
    }
}

@Composable
fun MemoryGameScreen() {
    var cards by remember { mutableStateOf(generateCards()) }
    var flippedCards = remember { mutableStateListOf<MemoryCard>() }
    val scope = rememberCoroutineScope()
    var allMatched by remember { mutableStateOf(false) }

    // Atualiza allMatched sempre que os cartões mudam
    LaunchedEffect(cards) {
        allMatched = cards.all { it.isMatched }
        Log.d("MemoryGame", "allMatched: $allMatched") // Log para depuração
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CardGrid(
                        cards = cards,
                        flippedCards = flippedCards,
                        onCardClicked = { card ->
                            handleCardClick(card, flippedCards, scope)
                        }
                    )
                    // Botão para reiniciar o jogo

                    Button(onClick = {
                        scope.launch {
                            resetAllCards(cards)
                            resetFlippedCards(flippedCards)
                            delay(300)
                            flippedCards.clear()
                            cards = generateCards()
                        }
                    }) {
                        Text("Reiniciar Jogo")
                    }

                }
            }
        }
    )
}

@Composable
fun CardGrid(
    cards: List<MemoryCard>,
    flippedCards: List<MemoryCard>,
    onCardClicked: (MemoryCard) -> Unit
) {
    val gridSize = 4
    Column {
        for (row in 0 until gridSize) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                for (column in 0 until gridSize) {
                    val index = row * gridSize + column
                    val card = cards[index]
                    val isFlipped = flippedCards.contains(card)

                    CardItem(card = card.copy(isFlipped = isFlipped), onClick = { onCardClicked(card) })
                }
            }
        }
    }
}

@Composable
fun CardItem(card: MemoryCard, onClick: () -> Unit) {
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(card.isFlipped || card.isMatched) {
        rotation.animateTo(if (card.isFlipped || card.isMatched) 180f else 0f, animationSpec = tween(durationMillis = 300))
    }

    Box(
        modifier = Modifier
            .size(90.dp)
            .padding(4.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                onClick = { onClick() },
                indication = rememberRipple(color = Color.Gray, radius = 90.dp),
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(rotationY = rotation.value)
                .clip(RoundedCornerShape(16.dp))
        ) {
            val imageResId = if (rotation.value < 90f) card.backImageResId else card.imageResId
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

private fun resetAllCards(cards: List<MemoryCard>) {
    for (card in cards) {
        card.isFlipped = false
        card.isMatched = false
    }
}

private fun handleCardClick(card: MemoryCard, flippedCards: MutableList<MemoryCard>, scope: CoroutineScope) {
    Log.d("MemoryGame", "Card clicked: ${card.imageResId}")
    if (flippedCards.size < 2 && !card.isFlipped && !card.isMatched) {
        Log.d("MemoryGame", "Flipping card: ${card.imageResId}")
        flipCard(card, flippedCards, scope)
    }
}

fun flipCard(card: MemoryCard, flippedCards: MutableList<MemoryCard>, scope: CoroutineScope) {
    card.isFlipped = true
    flippedCards.add(card)

    Log.d("MemoryGame", "Flipped card: ${card.imageResId}")

    if (flippedCards.size == 2) {
        scope.launch {
            handleCardMatch(flippedCards)
        }
    }
}

private suspend fun handleCardMatch(flippedCards: MutableList<MemoryCard>) {
    delay(600) // Tempo para a animação de flip

    if (flippedCards[0].imageResId == flippedCards[1].imageResId) {
        markCardsAsMatched(flippedCards)
    } else {
        resetFlippedCards(flippedCards)
    }
}

private fun markCardsAsMatched(flippedCards: MutableList<MemoryCard>) {
    // Marcar as cartas como combinadas
    flippedCards[0].isMatched = true
    flippedCards[1].isMatched = true
    // Não é necessário redefinir isFlipped, pois já está true
    Log.d("MemoryGame", "Cards matched: ${flippedCards[0].imageResId}")
    flippedCards.clear()
}

private fun resetFlippedCards(flippedCards: MutableList<MemoryCard>) {
    // Verifica se a lista tem pelo menos 2 cartas
    if (flippedCards.size >= 2) {
        // Apenas redefine o estado se as cartas não forem combinadas
        if (!flippedCards[0].isMatched && !flippedCards[1].isMatched) {
            flippedCards[0].isFlipped = false
            flippedCards[1].isFlipped = false
        }
        Log.d("MemoryGame", "Cards did not match: ${flippedCards[0].imageResId}, ${flippedCards[1].imageResId}")
    }
    flippedCards.clear()
}

fun generateCards(): List<MemoryCard> {
    val images = listOf(
        R.drawable.img1, R.drawable.img2, R.drawable.img3, R.drawable.img4,
        R.drawable.img5, R.drawable.img6, R.drawable.img7, R.drawable.img8
    )
    val cards = images.flatMap {
        listOf(MemoryCard(it.hashCode(), it), MemoryCard(it.hashCode() + 1, it))
    }
    Log.d("MemoryGame", "Generated cards: ${cards.map { it.imageResId }}")
    return cards.shuffled()
}