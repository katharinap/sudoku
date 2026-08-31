package com.katharina.sudoku.domain

import com.katharina.sudoku.domain.model.Difficulty
import com.katharina.sudoku.domain.model.SudokuBoard
import kotlin.random.Random

object SudokuCarver {

    private val Difficulty.targetClues: IntRange
        get() = when (this) {
            Difficulty.EASY -> 36..45
            Difficulty.MEDIUM -> 30..35
            Difficulty.HARD -> 25..29
            Difficulty.EXPERT -> 17..24
        }

    /**
     * Removes cells from a solved board while ensuring a unique solution exists.
     * The number of remaining cells will target the range specified by [difficulty].
     */
    fun carve(solvedBoard: SudokuBoard, difficulty: Difficulty): SudokuBoard {
        val targetRange = difficulty.targetClues
        val targetClues = Random.nextInt(targetRange.first, targetRange.last + 1)
        
        val cells = solvedBoard.cells.toMutableList()
        val positions = (0..80).shuffled().toMutableList()
        
        var currentClues = 81
        
        for (index in positions) {
            if (currentClues <= targetClues) break
            
            val originalCell = cells[index]
            if (originalCell.value == null) continue
            
            // Try removing the value
            cells[index] = originalCell.copy(value = null)
            
            // Check if still uniquely solvable
            if (SudokuSolver.countSolutions(SudokuBoard(cells), limit = 2) == 1) {
                currentClues--
            } else {
                // Not unique, put the value back
                cells[index] = originalCell
            }
        }
        
        // Mark all remaining filled cells as fixed
        val carvedCells = cells.map { cell ->
            if (cell.value != null) {
                cell.copy(isFixed = true)
            } else {
                cell.copy(isFixed = false, notes = emptySet())
            }
        }
        
        return SudokuBoard(carvedCells)
    }
}
