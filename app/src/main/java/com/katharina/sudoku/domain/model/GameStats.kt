package com.katharina.sudoku.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class GameStats(
    val difficulty: Difficulty,
    val gamesPlayed: Int,
    val gamesWon: Int,
    val bestTimeSeconds: Long
)
