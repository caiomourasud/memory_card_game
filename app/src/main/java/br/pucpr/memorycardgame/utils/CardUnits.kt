package br.pucpr.memorycardgame.utils

import androidx.compose.runtime.MutableState
import br.pucpr.memorycardgame.R
import br.pucpr.memorycardgame.models.MemoryCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun resetAllCards(cards: List<MemoryCard>) {
    for (card in cards) {
        card.isFlipped = false
        card.isMatched = false
    }
}

 fun handleCardClick(
     card: MemoryCard,
     flippedCards: MutableList<MemoryCard>,
     scope: CoroutineScope,
     allMatched: MutableState<Boolean>,
     cards: List<MemoryCard>,
     bestScores: MutableList<Int>,
     rounds: Int,
     incrementFlippedCount: () -> Unit,
     onCardsMatched: () -> Unit
) {
    if (flippedCards.size < 2 && !card.isFlipped && !card.isMatched) {
        incrementFlippedCount()
        flipCard(card, flippedCards, scope, allMatched, cards, bestScores, rounds, onCardsMatched)
    }
}

fun flipCard(
    card: MemoryCard,
    flippedCards: MutableList<MemoryCard>,
    scope: CoroutineScope,
    allMatched: MutableState<Boolean>,
    cards: List<MemoryCard>,
    bestScores: MutableList<Int>,
    rounds: Int,
    onCardsMatched: () -> Unit
) {
    card.isFlipped = true
    flippedCards.add(card)

    if (flippedCards.size == 2) {
        scope.launch {
            handleCardMatch(flippedCards, allMatched, cards, bestScores, rounds, onCardsMatched)
        }
    }
}

 suspend fun handleCardMatch(
     flippedCards: MutableList<MemoryCard>,
     allMatched: MutableState<Boolean>,
     cards: List<MemoryCard>,
     bestScores: MutableList<Int>,
     rounds: Int,
     onCardsMatched: () -> Unit
) {
    delay(600)

    if (flippedCards[0].imageResId == flippedCards[1].imageResId) {
        markCardsAsMatched(flippedCards, allMatched, cards, bestScores, rounds)
        onCardsMatched()
    } else {
        resetFlippedCards(flippedCards)
    }
}

 fun markCardsAsMatched(
     flippedCards: MutableList<MemoryCard>,
     allMatched: MutableState<Boolean>,
     cards: List<MemoryCard>,
     bestScores: MutableList<Int>,
     rounds: Int,
) {
    flippedCards[0].isMatched = true
    flippedCards[1].isMatched = true
    allMatched.value = cards.all { it.isMatched }

    if (allMatched.value) {
        bestScores.add(rounds)
    }

    flippedCards.clear()
}

 fun resetFlippedCards(flippedCards: MutableList<MemoryCard>) {
    if (flippedCards.size >= 2) {
        if (!flippedCards[0].isMatched && !flippedCards[1].isMatched) {
            flippedCards[0].isFlipped = false
            flippedCards[1].isFlipped = false
        }
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
    return cards.shuffled()
}