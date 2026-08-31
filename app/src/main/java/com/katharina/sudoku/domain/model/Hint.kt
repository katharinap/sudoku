package com.katharina.sudoku.domain.model

data class Hint(
    val position: Position,
    val value: Int,
    val explanation: String
)
