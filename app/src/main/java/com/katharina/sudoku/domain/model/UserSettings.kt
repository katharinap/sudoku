package com.katharina.sudoku.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val defaultDifficulty: Difficulty = Difficulty.EASY,
    val highlightSameNumbers: Boolean = true,
    val autoClearNotes: Boolean = true
)
