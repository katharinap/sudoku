package com.katharina.sudoku.presentation.menu

import com.katharina.sudoku.domain.model.GameStats

data class MenuUiState(
    val canContinue: Boolean = false,
    val stats: List<GameStats> = emptyList(),
    val isLoading: Boolean = true
)
