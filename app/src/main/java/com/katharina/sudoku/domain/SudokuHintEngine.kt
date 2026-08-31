package com.katharina.sudoku.domain

import com.katharina.sudoku.domain.model.Hint
import com.katharina.sudoku.domain.model.Position
import com.katharina.sudoku.domain.model.SudokuBoard

object SudokuHintEngine {

    fun getNextHint(board: SudokuBoard): Hint? {
        val cells = IntArray(81) { board.cells[it].value ?: 0 }
        
        // Map of empty indices to their candidates
        val candidatesMap = mutableMapOf<Int, Set<Int>>()
        for (i in 0..80) {
            if (cells[i] == 0) {
                val row = i / 9
                val col = i % 9
                val candidates = (1..9).filter { valToCheck ->
                    SudokuValidator.isValidPlacement(cells, row, col, valToCheck)
                }.toSet()
                candidatesMap[i] = candidates
            }
        }

        // 1. Naked Singles
        for ((index, candidates) in candidatesMap) {
            if (candidates.size == 1) {
                val value = candidates.first()
                return Hint(
                    position = Position(index / 9, index % 9),
                    value = value,
                    explanation = "Naked Single: Only $value can fit in this cell."
                )
            }
        }

        // 2. Hidden Singles
        // Check Rows
        for (row in 0..8) {
            val hint = findHiddenSingleInUnit(candidatesMap, (0..8).map { row * 9 + it }, "Row ${row + 1}")
            if (hint != null) return hint
        }

        // Check Columns
        for (col in 0..8) {
            val hint = findHiddenSingleInUnit(candidatesMap, (0..8).map { it * 9 + col }, "Column ${col + 1}")
            if (hint != null) return hint
        }

        // Check Boxes
        for (box in 0..8) {
            val boxIndices = mutableListOf<Int>()
            val startRow = (box / 3) * 3
            val startCol = (box % 3) * 3
            for (r in 0..2) {
                for (c in 0..2) {
                    boxIndices.add((startRow + r) * 9 + (startCol + c))
                }
            }
            val hint = findHiddenSingleInUnit(candidatesMap, boxIndices, "Box ${box + 1}")
            if (hint != null) return hint
        }

        return null
    }

    private fun findHiddenSingleInUnit(
        candidatesMap: Map<Int, Set<Int>>,
        unitIndices: List<Int>,
        unitName: String
    ): Hint? {
        for (value in 1..9) {
            val possibleIndices = unitIndices.filter { idx -> 
                candidatesMap[idx]?.contains(value) == true 
            }
            if (possibleIndices.size == 1) {
                val index = possibleIndices.first()
                return Hint(
                    position = Position(index / 9, index % 9),
                    value = value,
                    explanation = "Hidden Single: In $unitName, $value can only be placed in this cell."
                )
            }
        }
        return null
    }
}
