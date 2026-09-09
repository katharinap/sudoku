package com.katharina.sudoku.domain

import com.katharina.sudoku.domain.model.Cell
import com.katharina.sudoku.domain.model.Difficulty
import com.katharina.sudoku.domain.model.Position
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
     * Generates a new Sudoku puzzle for the given [difficulty].
     * It starts with a fully solved board and removes cells while ensuring a unique solution.
     */
    fun generate(difficulty: Difficulty): SudokuBoard {
        val solvedBoard = SudokuGenerator.generateSolvedBoard()
        val targetRange = difficulty.targetClues
        val targetClues = Random.nextInt(targetRange.first, targetRange.last + 1)
        
        val cells = IntArray(81) { solvedBoard.cells[it].value ?: 0 }
        val solution = IntArray(81) { solvedBoard.cells[it].solutionValue }
        val positions = (0..80).shuffled()
        
        var currentClues = 81
        
        for (index in positions) {
            if (currentClues <= targetClues) break
            
            val originalValue = cells[index]
            if (originalValue == 0) continue
            
            // Try removing the value
            cells[index] = 0
            
            // Check if still uniquely solvable
            if (SudokuSolver.countSolutions(cells, limit = 2) == 1) {
                currentClues--
            } else {
                // Not unique, put the value back
                cells[index] = originalValue
            }
        }
        
        // Convert back to SudokuBoard and mark filled cells as fixed
        val boardCells = (0..80).map { i ->
            val value = if (cells[i] == 0) null else cells[i]
            Cell(
                position = Position(i / 9, i % 9),
                value = value,
                solutionValue = solution[i],
                isFixed = value != null
            )
        }
        
        return SudokuBoard(boardCells)
    }
}
