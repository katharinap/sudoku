package com.katharina.sudoku.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class Cell(
    val position: Position,
    val value: Int? = null,
    val solutionValue: Int,
    val isFixed: Boolean = false,
    val notes: Set<Int> = emptySet()
) {
    init {
        value?.let {
            require(it in 1..9) { "Value must be between 1 and 9" }
        }
        require(solutionValue in 1..9) { "Solution value must be between 1 and 9" }
        notes.forEach {
            require(it in 1..9) { "Note must be between 1 and 9" }
        }
    }
}
