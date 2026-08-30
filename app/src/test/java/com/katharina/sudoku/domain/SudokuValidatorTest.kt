package com.katharina.sudoku.domain

import com.katharina.sudoku.domain.model.Cell
import com.katharina.sudoku.domain.model.Position
import com.katharina.sudoku.domain.model.SudokuBoard
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class SudokuValidatorTest {

    @Test
    fun `isValidPlacement returns true for valid placement in empty board`() {
        val board = SudokuBoard.empty()
        assertThat(SudokuValidator.isValidPlacement(board, 0, 0, 5)).isTrue()
    }

    @Test
    fun `isValidPlacement returns false for row conflict`() {
        val board = SudokuBoard.empty().copy(
            cells = SudokuBoard.empty().cells.map { 
                if (it.position == Position(0, 5)) it.copy(value = 5) else it 
            }
        )
        assertThat(SudokuValidator.isValidPlacement(board, 0, 0, 5)).isFalse()
    }

    @Test
    fun `isValidPlacement returns false for column conflict`() {
        val board = SudokuBoard.empty().copy(
            cells = SudokuBoard.empty().cells.map { 
                if (it.position == Position(5, 0)) it.copy(value = 5) else it 
            }
        )
        assertThat(SudokuValidator.isValidPlacement(board, 0, 0, 5)).isFalse()
    }

    @Test
    fun `isValidPlacement returns false for box conflict`() {
        val board = SudokuBoard.empty().copy(
            cells = SudokuBoard.empty().cells.map { 
                if (it.position == Position(1, 1)) it.copy(value = 5) else it 
            }
        )
        assertThat(SudokuValidator.isValidPlacement(board, 0, 0, 5)).isFalse()
    }

    @Test
    fun `isBoardComplete returns false for empty board`() {
        assertThat(SudokuValidator.isBoardComplete(SudokuBoard.empty())).isFalse()
    }

    @Test
    fun `isBoardComplete returns true for a valid solved board`() {
        val solvedBoard = createSolvedBoard()
        assertThat(SudokuValidator.isBoardComplete(solvedBoard)).isTrue()
    }

    @Test
    fun `findConflicts detects all duplicate positions`() {
        // Create board with duplicates in row 0 (positions (0,0) and (0,1) both have 5)
        val cells = SudokuBoard.empty().cells.map {
            when (it.position) {
                Position(0, 0) -> it.copy(value = 5)
                Position(0, 1) -> it.copy(value = 5)
                else -> it
            }
        }
        val board = SudokuBoard(cells)
        
        val conflicts = SudokuValidator.findConflicts(board)
        assertThat(conflicts).containsExactly(Position(0, 0), Position(0, 1))
    }

    private fun createSolvedBoard(): SudokuBoard {
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
            Cell(Position(i / 9, i % 9), value = value, isFixed = true)
        }
        return SudokuBoard(cells)
    }
}
