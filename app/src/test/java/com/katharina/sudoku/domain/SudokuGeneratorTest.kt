package com.katharina.sudoku.domain

import com.katharina.sudoku.domain.model.SudokuBoard
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class SudokuGeneratorTest {

    @Test
    fun `generateSolvedBoard produces a complete and valid board`() {
        val board = SudokuGenerator.generateSolvedBoard()
        
        assertThat(board.cells).hasSize(81)
        assertThat(board.cells.all { it.value != null }).isTrue()
        assertThat(SudokuValidator.isBoardComplete(board)).isTrue()
    }

    @Test
    fun `generateSolvedBoard produces different boards on subsequent calls`() {
        val board1 = SudokuGenerator.generateSolvedBoard()
        val board2 = SudokuGenerator.generateSolvedBoard()
        
        // Highly unlikely to get two identical boards with randomization
        assertThat(board1).isNotEqualTo(board2)
    }
}
