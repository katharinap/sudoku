package com.katharina.sudoku.domain

import com.katharina.sudoku.domain.model.Cell
import com.katharina.sudoku.domain.model.Position
import com.katharina.sudoku.domain.model.SudokuBoard

object SudokuGenerator {

    /**
     * Generates a fully solved, valid Sudoku board using backtracking and randomization.
     */
    fun generateSolvedBoard(): SudokuBoard {
        val cells = IntArray(81)
        fillBoard(cells, 0)
        val boardCells = cells.mapIndexed { index, value ->
            Cell(Position(index / 9, index % 9), value = value)
        }
        return SudokuBoard(boardCells)
    }

    private fun fillBoard(cells: IntArray, index: Int): Boolean {
        if (index == 81) return true

        val row = index / 9
        val col = index % 9
        val numbers = (1..9).shuffled()

        for (num in numbers) {
            if (SudokuValidator.isValidPlacement(cells, row, col, num)) {
                cells[index] = num
                if (fillBoard(cells, index + 1)) {
                    return true
                }
            }
            // Backtrack
            cells[index] = 0
        }

        return false
    }
}
