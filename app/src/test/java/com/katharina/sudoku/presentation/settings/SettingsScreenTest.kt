package com.katharina.sudoku.presentation.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun settingsScreenRendersCorrectly() {
        val uiState = SettingsUiState(isLoading = false)

        composeTestRule.setContent {
            SettingsContent(
                uiState = uiState,
                onBackClick = {},
                onThemeModeChanged = {},
                onSoundChanged = {},
                onHapticsChanged = {},
                onDefaultDifficultyChanged = {},
                onHighlightSameNumbersChanged = {},
                onAutoClearNotesChanged = {}
            )
        }

        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
        composeTestRule.onNodeWithText("Appearance").assertIsDisplayed()
        composeTestRule.onNodeWithText("App Theme").assertIsDisplayed()
        
        // Scroll to "Game" if needed
        composeTestRule.onNodeWithText("Game").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Sound Effects").assertIsDisplayed()
        
        // Scroll to "Defaults"
        composeTestRule.onNodeWithText("Defaults").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun clickingDifficultyDropdownShowsOptions() {
        val uiState = SettingsUiState(isLoading = false)

        composeTestRule.setContent {
            SettingsContent(
                uiState = uiState,
                onBackClick = {},
                onThemeModeChanged = {},
                onSoundChanged = {},
                onHapticsChanged = {},
                onDefaultDifficultyChanged = {},
                onHighlightSameNumbersChanged = {},
                onAutoClearNotesChanged = {}
            )
        }

        composeTestRule.onNodeWithText("Default Difficulty").performScrollTo().performClick()
        composeTestRule.onNodeWithText("EXPERT").assertIsDisplayed()
    }
}
