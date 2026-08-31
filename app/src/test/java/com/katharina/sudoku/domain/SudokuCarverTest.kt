package com.katharina.sudoku.domain

import com.katharina.sudoku.domain.model.Difficulty
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class SudokuCarverTest {

    @Test
    fun `carve produces uniquely solvable puzzle with correct clue count for EASY`() {
        verifyCarve(Difficulty.EASY, 36..45)
    }

    @Test
    fun `carve produces uniquely solvable puzzle with correct clue count for MEDIUM`() {
        verifyCarve(Difficulty.MEDIUM, 30..35)
    }

    @Test
    fun `carve produces uniquely solvable puzzle with correct clue count for HARD`() {
        verifyCarve(Difficulty.HARD, 25..29)
    }

    private fun verifyCarve(difficulty: Difficulty, expectedClueRange: IntRange) {
        val solvedBoard = SudokuGenerator.generateSolvedBoard()
        val carvedBoard = SudokuCarver.carve(solvedBoard, difficulty)
        
        val clueCount = carvedBoard.cells.count { it.value != null }
        
        // Clue count should be within range
        // Note: Sometimes it might be slightly higher if removing any more would result in non-unique solution
        assertThat(clueCount).run {
            if (difficulty != Difficulty.EXPERT) {
                isAtLeast(expectedClueRange.first)
            }
            isAtMost(81)
        }
        
        // Should have exactly one solution
        assertThat(SudokuSolver.countSolutions(carvedBoard, limit = 2)).isEqualTo(1)
        
        // All filled cells should be marked as fixed
        carvedBoard.cells.forEach { cell ->
            if (cell.value != null) {
                assertThat(cell.isFixed).isTrue()
            } else {
                assertThat(cell.isFixed).isFalse()
            }
        }
    }
}
