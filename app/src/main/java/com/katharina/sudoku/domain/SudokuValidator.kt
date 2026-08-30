package com.katharina.sudoku.domain

import com.katharina.sudoku.domain.model.Position
import com.katharina.sudoku.domain.model.SudokuBoard

object SudokuValidator {

    /**
     * Checks if placing [value] at ([row], [col]) violates Sudoku rules.
     * It does not check if the cell already has a value, only if the new value would be valid.
     */
    fun isValidPlacement(board: SudokuBoard, row: Int, col: Int, value: Int): Boolean {
        // Check row
        if (board.getRow(row).any { it.value == value && it.position.column != col }) {
            return false
        }

        // Check column
        if (board.getColumn(col).any { it.value == value && it.position.row != row }) {
            return false
        }

        // Check box
        val boxIndex = board.getBoxIndex(row, col)
        if (board.getBox(boxIndex).any { it.value == value && (it.position.row != row || it.position.column != col) }) {
            return false
        }

        return true
    }

    /**
     * Checks if the board is fully filled and all placements follow Sudoku rules.
     */
    fun isBoardComplete(board: SudokuBoard): Boolean {
        if (board.cells.any { it.value == null }) return false

        return board.cells.all { cell ->
            isValidPlacement(board, cell.position.row, cell.position.column, cell.value!!)
        }
    }

    /**
     * Finds all positions where a conflict exists.
     * A conflict is a cell whose value appears more than once in its row, column, or box.
     */
    fun findConflicts(board: SudokuBoard): List<Position> {
        val conflicts = mutableSetOf<Position>()

        // Check rows
        for (row in 0..8) {
            val cellsInRow = board.getRow(row).filter { it.value != null }
            cellsInRow.groupBy { it.value }.filter { it.value.size > 1 }.values.forEach { duplicatedCells ->
                conflicts.addAll(duplicatedCells.map { it.position })
            }
        }

        // Check columns
        for (col in 0..8) {
            val cellsInCol = board.getColumn(col).filter { it.value != null }
            cellsInCol.groupBy { it.value }.filter { it.value.size > 1 }.values.forEach { duplicatedCells ->
                conflicts.addAll(duplicatedCells.map { it.position })
            }
        }

        // Check boxes
        for (box in 0..8) {
            val cellsInBox = board.getBox(box).filter { it.value != null }
            cellsInBox.groupBy { it.value }.filter { it.value.size > 1 }.values.forEach { duplicatedCells ->
                conflicts.addAll(duplicatedCells.map { it.position })
            }
        }

        return conflicts.toList()
    }
}
