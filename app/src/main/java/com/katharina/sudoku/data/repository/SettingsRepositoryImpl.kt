package com.katharina.sudoku.data.repository

import com.katharina.sudoku.data.local.SettingsDataSource
import com.katharina.sudoku.domain.model.ThemeMode
import com.katharina.sudoku.domain.model.UserSettings
import com.katharina.sudoku.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataSource: SettingsDataSource
) : SettingsRepository {

    override val userSettings: Flow<UserSettings> = dataSource.userSettings

    override suspend fun updateThemeMode(themeMode: ThemeMode) {
        dataSource.updateThemeMode(themeMode)
    }

    override suspend fun updateHighlightSameNumbers(isEnabled: Boolean) {
        dataSource.updateHighlightSameNumbers(isEnabled)
    }

    override suspend fun updateAutoClearNotes(isEnabled: Boolean) {
        dataSource.updateAutoClearNotes(isEnabled)
    }
}
