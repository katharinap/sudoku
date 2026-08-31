package com.katharina.sudoku.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Cell(
    val position: Position,
    val value: Int? = null,
    val isFixed: Boolean = false,
    val notes: Set<Int> = emptySet()
) {
    init {
        value?.let {
            require(it in 1..9) { "Value must be between 1 and 9" }
        }
        notes.forEach {
            require(it in 1..9) { "Note must be between 1 and 9" }
        }
    }
}
