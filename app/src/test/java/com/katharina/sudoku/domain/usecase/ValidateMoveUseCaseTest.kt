package com.katharina.sudoku.domain.usecase

import com.katharina.sudoku.domain.model.SudokuBoard
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class ValidateMoveUseCaseTest {

    private val validateMoveUseCase = ValidateMoveUseCase()

    @Test
    fun `invoke returns true for valid move on empty board`() {
        assertThat(validateMoveUseCase(SudokuBoard.empty(), 0, 0, 5)).isTrue()
    }

    @Test
    fun `invoke returns false for invalid move (row conflict)`() {
        // Create board with 5 in row 0
        val cells = SudokuBoard.empty().cells.toMutableList()
        cells[5] = cells[5].copy(value = 5)
        val board = SudokuBoard(cells)
        
        assertThat(validateMoveUseCase(board, 0, 0, 5)).isFalse()
    }
}
