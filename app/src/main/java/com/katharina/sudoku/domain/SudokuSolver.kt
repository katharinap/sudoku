package com.katharina.sudoku.domain

import com.katharina.sudoku.domain.model.SudokuBoard

object SudokuSolver {

    /**
     * Solves the given Sudoku board.
     * Returns a new solved board if a solution exists, or null otherwise.
     */
    fun solve(board: SudokuBoard): SudokuBoard? {
        if (SudokuValidator.findConflicts(board).isNotEmpty()) return null
        
        val cells = IntArray(81) { board.cells[it].value ?: 0 }
        if (solveRecursive(cells, 0)) {
            val solvedCells = board.cells.mapIndexed { index, cell ->
                cell.copy(value = if (cells[index] == 0) null else cells[index])
            }
            return SudokuBoard(solvedCells)
        }
        return null
    }

    /**
     * Counts the number of valid solutions for a given board, up to [limit].
     */
    fun countSolutions(board: SudokuBoard, limit: Int = 2): Int {
        if (SudokuValidator.findConflicts(board).isNotEmpty()) return 0
        
        val cells = IntArray(81) { board.cells[it].value ?: 0 }
        return countSolutionsRecursive(cells, 0, limit, 0)
    }

    private fun solveRecursive(cells: IntArray, index: Int): Boolean {
        if (index == 81) return true

        if (cells[index] != 0) {
            return solveRecursive(cells, index + 1)
        }

        val row = index / 9
        val col = index % 9
        for (num in 1..9) {
            if (SudokuValidator.isValidPlacement(cells, row, col, num)) {
                cells[index] = num
                if (solveRecursive(cells, index + 1)) {
                    return true
                }
            }
        }

        // Backtrack
        cells[index] = 0
        return false
    }

    private fun countSolutionsRecursive(
        cells: IntArray,
        index: Int,
        limit: Int,
        count: Int
    ): Int {
        var currentCount = count
        if (index == 81) return currentCount + 1

        if (cells[index] != 0) {
            return countSolutionsRecursive(cells, index + 1, limit, currentCount)
        }

        val row = index / 9
        val col = index % 9
        for (num in 1..9) {
            if (SudokuValidator.isValidPlacement(cells, row, col, num)) {
                cells[index] = num
                currentCount = countSolutionsRecursive(cells, index + 1, limit, currentCount)
                if (currentCount >= limit) {
                    // Important: Reset before returning early during backtracking
                    cells[index] = 0
                    return currentCount
                }
            }
        }

        // Backtrack
        cells[index] = 0
        return currentCount
    }
}
