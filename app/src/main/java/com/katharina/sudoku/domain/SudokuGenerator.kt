package com.katharina.sudoku.domain

import com.katharina.sudoku.domain.model.Cell
import com.katharina.sudoku.domain.model.SudokuBoard

object SudokuGenerator {

    /**
     * Generates a fully solved, valid Sudoku board using backtracking and randomization.
     */
    fun generateSolvedBoard(): SudokuBoard {
        val cells = SudokuBoard.empty().cells.toMutableList()
        fillBoard(cells, 0)
        return SudokuBoard(cells)
    }

    private fun fillBoard(cells: MutableList<Cell>, index: Int): Boolean {
        if (index == 81) return true

        val row = index / 9
        val col = index % 9
        val numbers = (1..9).shuffled()

        for (num in numbers) {
            // Temporarily update the cell to check validity
            val originalCell = cells[index]
            cells[index] = originalCell.copy(value = num)

            if (SudokuValidator.isValidPlacement(SudokuBoard(cells), row, col, num)) {
                if (fillBoard(cells, index + 1)) {
                    return true
                }
            }

            // Backtrack
            cells[index] = originalCell
        }

        return false
    }
}
