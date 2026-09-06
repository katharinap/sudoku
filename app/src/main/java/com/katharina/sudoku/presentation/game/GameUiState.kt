package com.katharina.sudoku.presentation.game

import androidx.compose.runtime.Immutable
import com.katharina.sudoku.domain.model.Difficulty
import com.katharina.sudoku.domain.model.Position
import com.katharina.sudoku.domain.model.SudokuBoard

@Immutable
data class GameUiState(
    val board: SudokuBoard = SudokuBoard.empty(),
    val selectedPosition: Position? = null,
    val isNoteModeEnabled: Boolean = false,
    val timerSeconds: Long = 0,
    val mistakeCount: Int = 0,
    val difficulty: Difficulty = Difficulty.EASY,
    val isComplete: Boolean = false,
    val isPaused: Boolean = false,
    val errorPosition: Position? = null,
    val conflictPositions: Set<Position> = emptySet(),
    val message: String? = null
)
