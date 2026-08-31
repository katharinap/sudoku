package com.katharina.sudoku.data.mapper

import com.katharina.sudoku.domain.model.Difficulty
import com.katharina.sudoku.domain.model.GameState
import com.katharina.sudoku.domain.model.GameStats
import com.katharina.sudoku.domain.model.SudokuBoard
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class MapperTest {

    @Test
    fun `GameState round trip mapping`() {
        val board = SudokuBoard.empty()
        val gameState = GameState(
            board = board,
            difficulty = Difficulty.MEDIUM,
            timerSeconds = 360,
            mistakes = 2
        )

        val entity = gameState.toEntity()
        val mappedBack = entity.toDomain()

        assertThat(mappedBack).isEqualTo(gameState)
    }

    @Test
    fun `GameStats round trip mapping`() {
        val stats = GameStats(
            difficulty = Difficulty.EXPERT,
            gamesPlayed = 5,
            gamesWon = 3,
            bestTimeSeconds = 1200
        )

        val entity = stats.toEntity()
        val mappedBack = entity.toDomain()

        assertThat(mappedBack).isEqualTo(stats)
    }
}
