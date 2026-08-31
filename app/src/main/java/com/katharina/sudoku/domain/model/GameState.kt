package com.katharina.sudoku.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class GameState(
    val board: SudokuBoard,
    val difficulty: Difficulty,
    val timerSeconds: Long,
    val mistakes: Int
)
