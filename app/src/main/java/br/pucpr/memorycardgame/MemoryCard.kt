package br.pucpr.memorycardgame

data class MemoryCard(
    val id: Int,
    val imageResId: Int,
    val backImageResId: Int = R.drawable.card_back,
    var isFlipped: Boolean = false,
    var isMatched: Boolean = false
)