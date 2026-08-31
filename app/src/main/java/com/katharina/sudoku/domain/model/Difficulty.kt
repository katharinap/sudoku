package com.katharina.sudoku.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class Difficulty {
    EASY,
    MEDIUM,
    HARD,
    EXPERT
}
