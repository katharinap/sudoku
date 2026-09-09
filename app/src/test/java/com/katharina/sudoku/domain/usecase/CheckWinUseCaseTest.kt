package com.katharina.sudoku.domain.usecase

import com.katharina.sudoku.domain.model.Cell
import com.katharina.sudoku.domain.model.Position
import com.katharina.sudoku.domain.model.SudokuBoard
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class CheckWinUseCaseTest {

    private val checkWinUseCase = CheckWinUseCase()

    @Test
    fun `invoke returns true for a solved board`() {
        val solvedValues = listOf(
            5, 3, 4, 6, 7, 8, 9, 1, 2,
            6, 7, 2, 1, 9, 5, 3, 4, 8,
            1, 9, 8, 3, 4, 2, 5, 6, 7,
            8, 5, 9, 7, 6, 1, 4, 2, 3,
            4, 2, 6, 8, 5, 3, 7, 9, 1,
            7, 1, 3, 9, 2, 4, 8, 5, 6,
            9, 6, 1, 5, 3, 7, 2, 8, 4,
            2, 8, 7, 4, 1, 9, 6, 3, 5,
            3, 4, 5, 2, 8, 6, 1, 7, 9
        )
        val cells = solvedValues.mapIndexed { i, value ->
            Cell(Position(i / 9, i % 9), value = value, solutionValue = value)
        }
        val board = SudokuBoard(cells)
        
        assertThat(checkWinUseCase(board)).isTrue()
    }

    @Test
    fun `invoke returns false for an empty board`() {
        assertThat(checkWinUseCase(SudokuBoard.empty())).isFalse()
    }
}
