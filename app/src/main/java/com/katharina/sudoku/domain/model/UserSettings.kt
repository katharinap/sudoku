package com.katharina.sudoku.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserSettings(
    val isDarkMode: Boolean? = null,
    val isSoundEnabled: Boolean = true,
    val isHapticsEnabled: Boolean = true,
    val defaultDifficulty: Difficulty = Difficulty.EASY,
    val highlightSameNumbers: Boolean = true,
    val autoClearNotes: Boolean = true
)
