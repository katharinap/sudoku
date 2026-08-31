package com.katharina.sudoku.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Position(val row: Int, val column: Int) {
    init {
        require(row in 0..8) { "Row must be between 0 and 8" }
        require(column in 0..8) { "Column must be between 0 and 8" }
    }
}
