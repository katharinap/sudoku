package com.katharina.sudoku.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.katharina.sudoku.domain.model.Difficulty
import com.katharina.sudoku.domain.model.ThemeMode
import com.katharina.sudoku.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val IS_SOUND_ENABLED = booleanPreferencesKey("is_sound_enabled")
        val IS_HAPTICS_ENABLED = booleanPreferencesKey("is_haptics_enabled")
        val DEFAULT_DIFFICULTY = stringPreferencesKey("default_difficulty")
        val HIGHLIGHT_SAME_NUMBERS = booleanPreferencesKey("highlight_same_numbers")
        val AUTO_CLEAR_NOTES = booleanPreferencesKey("auto_clear_notes")
    }

    val userSettings: Flow<UserSettings> = dataStore.data.map { preferences ->
        UserSettings(
            themeMode = preferences[Keys.THEME_MODE]?.let {
                try {
                    ThemeMode.valueOf(it)
                } catch (e: IllegalArgumentException) {
                    ThemeMode.SYSTEM
                }
            } ?: ThemeMode.SYSTEM,
            isSoundEnabled = preferences[Keys.IS_SOUND_ENABLED] ?: true,
            isHapticsEnabled = preferences[Keys.IS_HAPTICS_ENABLED] ?: true,
            defaultDifficulty = preferences[Keys.DEFAULT_DIFFICULTY]?.let {
                Difficulty.valueOf(it)
            } ?: Difficulty.EASY,
            highlightSameNumbers = preferences[Keys.HIGHLIGHT_SAME_NUMBERS] ?: true,
            autoClearNotes = preferences[Keys.AUTO_CLEAR_NOTES] ?: true
        )
    }

    suspend fun updateThemeMode(themeMode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[Keys.THEME_MODE] = themeMode.name
        }
    }

    suspend fun updateSoundEnabled(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.IS_SOUND_ENABLED] = isEnabled
        }
    }

    suspend fun updateHapticsEnabled(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.IS_HAPTICS_ENABLED] = isEnabled
        }
    }

    suspend fun updateDefaultDifficulty(difficulty: Difficulty) {
        dataStore.edit { preferences ->
            preferences[Keys.DEFAULT_DIFFICULTY] = difficulty.name
        }
    }

    suspend fun updateHighlightSameNumbers(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.HIGHLIGHT_SAME_NUMBERS] = isEnabled
        }
    }

    suspend fun updateAutoClearNotes(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.AUTO_CLEAR_NOTES] = isEnabled
        }
    }
}
