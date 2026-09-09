package com.katharina.sudoku.domain

import com.katharina.sudoku.domain.model.Cell
import com.katharina.sudoku.domain.model.Position
import com.katharina.sudoku.domain.model.SudokuBoard
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class SudokuSolverTest {

    @Test
    fun `solve returns solved board for valid easy puzzle`() {
        val board = createEasyPuzzle()
        val solvedBoard = SudokuSolver.solve(board)
        
        assertThat(solvedBoard).isNotNull()
        assertThat(SudokuValidator.isBoardComplete(solvedBoard!!)).isTrue()
    }

    @Test
    fun `solve returns null for invalid board with conflict`() {
        // Create board with conflict (two 5s in first row)
        val cells = SudokuBoard.empty().cells.toMutableList()
        cells[0] = cells[0].copy(value = 5)
        cells[1] = cells[1].copy(value = 5)
        val board = SudokuBoard(cells)
        
        val solvedBoard = SudokuSolver.solve(board)
        assertThat(solvedBoard).isNull()
    }

    @Test
    fun `countSolutions returns 1 for uniquely solvable puzzle`() {
        val board = createEasyPuzzle()
        val count = SudokuSolver.countSolutions(board, limit = 2)
        assertThat(count).isEqualTo(1)
    }

    @Test
    fun `countSolutions returns 2 for puzzle with multiple solutions`() {
        // Empty board has many solutions
        val count = SudokuSolver.countSolutions(SudokuBoard.empty(), limit = 2)
        assertThat(count).isEqualTo(2)
    }

    @Test
    fun `solve handles difficult puzzle`() {
        // "World's Hardest Sudoku" by Arto Inkala (2012)
        val board = createDifficultPuzzle()
        val solvedBoard = SudokuSolver.solve(board)
        
        assertThat(solvedBoard).isNotNull()
        assertThat(SudokuValidator.isBoardComplete(solvedBoard!!)).isTrue()
    }

    private fun createEasyPuzzle(): SudokuBoard {
        val values = listOf(
            5, 3, 0, 0, 7, 0, 0, 0, 0,
            6, 0, 0, 1, 9, 5, 0, 0, 0,
            0, 9, 8, 0, 0, 0, 0, 6, 0,
            8, 0, 0, 0, 6, 0, 0, 0, 3,
            4, 0, 0, 8, 0, 3, 0, 0, 1,
            7, 0, 0, 0, 2, 0, 0, 0, 6,
            0, 6, 0, 0, 0, 0, 2, 8, 0,
            0, 0, 0, 4, 1, 9, 0, 0, 5,
            0, 0, 0, 0, 8, 0, 0, 7, 9
        )
        val cells = values.mapIndexed { i, value ->
            Cell(
                position = Position(i / 9, i % 9),
                value = if (value == 0) null else value,
                solutionValue = 1 // Dummy
            )
        }
        return SudokuBoard(cells)
    }

    private fun createDifficultPuzzle(): SudokuBoard {
        val values = listOf(
            8, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 3, 6, 0, 0, 0, 0, 0,
            0, 7, 0, 0, 9, 0, 2, 0, 0,
            0, 5, 0, 0, 0, 7, 0, 0, 0,
            0, 0, 0, 0, 4, 5, 7, 0, 0,
            0, 0, 0, 1, 0, 0, 0, 3, 0,
            0, 0, 1, 0, 0, 0, 0, 6, 8,
            0, 0, 8, 5, 0, 0, 0, 1, 0,
            0, 9, 0, 0, 0, 0, 4, 0, 0
        )
        val cells = values.mapIndexed { i, value ->
            Cell(
                position = Position(i / 9, i % 9),
                value = if (value == 0) null else value,
                solutionValue = 1 // Dummy
            )
        }
        return SudokuBoard(cells)
    }
}
