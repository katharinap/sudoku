package com.katharina.sudoku.domain.usecase

import com.katharina.sudoku.domain.model.Difficulty
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class GenerateNewGameUseCaseTest {

    private val generateNewGameUseCase = GenerateNewGameUseCase()

    @Test
    fun `invoke returns a valid puzzle board for EASY difficulty`() {
        val board = generateNewGameUseCase(Difficulty.EASY)
        
        assertThat(board.cells).hasSize(81)
        // Verify it's a puzzle (has empty cells)
        assertThat(board.cells.any { it.value == null }).isTrue()
        // Verify clues are fixed
        board.cells.filter { it.value != null }.forEach {
            assertThat(it.isFixed).isTrue()
        }
    }
}
