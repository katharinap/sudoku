package com.katharina.sudoku.presentation.game

import com.katharina.sudoku.domain.model.Difficulty
import com.katharina.sudoku.domain.model.Position
import com.katharina.sudoku.domain.model.SudokuBoard
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class GameUiStateTest {

    @Test
    fun `initial state has correct default values`() {
        val state = GameUiState()
        
        assertThat(state.board).isEqualTo(SudokuBoard.empty())
        assertThat(state.selectedPosition).isNull()
        assertThat(state.isNoteModeEnabled).isFalse()
        assertThat(state.timerSeconds).isEqualTo(0L)
        assertThat(state.mistakeCount).isEqualTo(0)
        assertThat(state.difficulty).isEqualTo(Difficulty.EASY)
        assertThat(state.isComplete).isFalse()
        assertThat(state.isPaused).isFalse()
    }

    @Test
    fun `copy creates a modified state correctly`() {
        val initialState = GameUiState()
        val newPosition = Position(1, 1)
        val newState = initialState.copy(
            selectedPosition = newPosition,
            mistakeCount = 1,
            isNoteModeEnabled = true
        )
        
        assertThat(newState.selectedPosition).isEqualTo(newPosition)
        assertThat(newState.mistakeCount).isEqualTo(1)
        assertThat(newState.isNoteModeEnabled).isTrue()
        
        // Unchanged values should remain the same
        assertThat(newState.board).isEqualTo(initialState.board)
        assertThat(newState.difficulty).isEqualTo(initialState.difficulty)
    }
}
