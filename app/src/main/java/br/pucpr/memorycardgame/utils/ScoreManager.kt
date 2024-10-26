package br.pucpr.memorycardgame.utils

import android.content.Context
import android.content.SharedPreferences

class ScoreManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("MemoryCardGamePrefs", Context.MODE_PRIVATE)

    fun saveScore(score: Int) {
        val scores = getScores().toMutableList()
        scores.add(score)
        sharedPreferences.edit().putString("scores", scores.joinToString(",")).apply()
    }

    fun getScores(): List<Int> {
        val scoresString = sharedPreferences.getString("scores", "")
        return if (!scoresString.isNullOrEmpty()) {
            scoresString.split(",").mapNotNull { it.toIntOrNull() } // Usa mapNotNull para evitar exceções
        } else {
            emptyList()
        }
    }

    fun getLowestScore(): Int {
        val scores = getScores().toMutableList()
        return scores.minOrNull() ?: 0
    }

    fun clearScores() {
        sharedPreferences.edit().remove("scores").apply()
    }
}