package com.katharina.sudoku.domain.repository

import com.katharina.sudoku.domain.model.Difficulty
import com.katharina.sudoku.domain.model.ThemeMode
import com.katharina.sudoku.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val userSettings: Flow<UserSettings>
    suspend fun updateThemeMode(themeMode: ThemeMode)
    suspend fun updateDefaultDifficulty(difficulty: Difficulty)
    suspend fun updateHighlightSameNumbers(isEnabled: Boolean)
    suspend fun updateAutoClearNotes(isEnabled: Boolean)
}
