package com.katharina.sudoku.domain

import com.katharina.sudoku.domain.model.Cell
import com.katharina.sudoku.domain.model.Position
import com.katharina.sudoku.domain.model.SudokuBoard

object SudokuValidator {

    /**
     * Checks if placing [value] at ([row], [col]) violates Sudoku rules.
     * It does not check if the cell already has a value, only if the new value would be valid.
     */
    fun isValidPlacement(board: SudokuBoard, row: Int, col: Int, value: Int): Boolean {
        val cells = IntArray(81) { board.cells[it].value ?: 0 }
        return isValidPlacement(cells, row, col, value)
    }

    /**
     * Optimized version of [isValidPlacement] that works directly on an IntArray.
     * 0 represents an empty cell.
     */
    fun isValidPlacement(cells: IntArray, row: Int, col: Int, value: Int): Boolean {
        // Check row
        for (c in 0..8) {
            if (c != col && cells[row * 9 + c] == value) {
                return false
            }
        }

        // Check column
        for (r in 0..8) {
            if (r != row && cells[r * 9 + col] == value) {
                return false
            }
        }

        // Check box
        val boxRowStart = (row / 3) * 3
        val boxColStart = (col / 3) * 3
        for (r in boxRowStart until boxRowStart + 3) {
            for (c in boxColStart until boxColStart + 3) {
                if ((r != row || c != col) && cells[r * 9 + c] == value) {
                    return false
                }
            }
        }

        return true
    }

    /**
     * Checks if the board is fully filled and all placements follow Sudoku rules.
     */
    fun isBoardComplete(board: SudokuBoard): Boolean {
        val cells = IntArray(81) { board.cells[it].value ?: 0 }
        if (cells.any { it == 0 }) return false

        for (i in 0..80) {
            if (!isValidPlacement(cells, i / 9, i % 9, cells[i])) {
                return false
            }
        }
        return true
    }

    /**
     * Finds all positions where a conflict exists.
     * A conflict is a cell whose value appears more than once in its row, column, or box.
     */
    fun findConflicts(board: SudokuBoard): List<Position> {
        val cells = IntArray(81) { board.cells[it].value ?: 0 }
        val conflicts = mutableSetOf<Position>()

        for (i in 0..80) {
            val value = cells[i]
            if (value != 0) {
                if (!isValidPlacement(cells, i / 9, i % 9, value)) {
                    conflicts.add(Position(i / 9, i % 9))
                }
            }
        }

        return conflicts.toList()
    }
}
