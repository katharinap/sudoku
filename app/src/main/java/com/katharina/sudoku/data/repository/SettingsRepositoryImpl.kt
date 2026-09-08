package com.katharina.sudoku.data.repository

import com.katharina.sudoku.data.local.SettingsDataSource
import com.katharina.sudoku.domain.model.Difficulty
import com.katharina.sudoku.domain.model.UserSettings
import com.katharina.sudoku.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataSource: SettingsDataSource
) : SettingsRepository {

    override val userSettings: Flow<UserSettings> = dataSource.userSettings

    override suspend fun updateDarkMode(isDarkMode: Boolean?) {
        dataSource.updateDarkMode(isDarkMode)
    }

    override suspend fun updateSoundEnabled(isEnabled: Boolean) {
        dataSource.updateSoundEnabled(isEnabled)
    }

    override suspend fun updateHapticsEnabled(isEnabled: Boolean) {
        dataSource.updateHapticsEnabled(isEnabled)
    }

    override suspend fun updateDefaultDifficulty(difficulty: Difficulty) {
        dataSource.updateDefaultDifficulty(difficulty)
    }

    override suspend fun updateHighlightSameNumbers(isEnabled: Boolean) {
        dataSource.updateHighlightSameNumbers(isEnabled)
    }

    override suspend fun updateAutoClearNotes(isEnabled: Boolean) {
        dataSource.updateAutoClearNotes(isEnabled)
    }
}
