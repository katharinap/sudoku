package com.katharina.sudoku.presentation.settings

import com.katharina.sudoku.domain.model.UserSettings

data class SettingsUiState(
    val settings: UserSettings = UserSettings(),
    val isLoading: Boolean = true
)
