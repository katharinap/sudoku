package com.katharina.sudoku.presentation.stats

import com.katharina.sudoku.domain.model.Difficulty
import com.katharina.sudoku.domain.model.GameStats

data class StatsUiState(
    val statsByDifficulty: Map<Difficulty, GameStats> = emptyMap(),
    val isLoading: Boolean = true
)
