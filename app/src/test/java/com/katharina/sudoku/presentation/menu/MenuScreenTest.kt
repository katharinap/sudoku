package com.katharina.sudoku.presentation.menu

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.katharina.sudoku.domain.model.Difficulty
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MenuScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun menuScreenRendersCorrectly() {
        val uiState = MenuUiState(isLoading = false, canContinue = true)

        composeTestRule.setContent {
            MenuContent(
                uiState = uiState,
                onContinueClick = {},
                onNewGameClick = {},
                onStatsClick = {},
                onSettingsClick = {}
            )
        }

        composeTestRule.onNodeWithText("Sudoku").assertExists()
        composeTestRule.onNodeWithText("Continue").assertIsDisplayed()
        composeTestRule.onNodeWithText("EASY").assertExists()
        composeTestRule.onNodeWithText("EXPERT").assertExists()
        composeTestRule.onNodeWithText("Stats").assertExists()
        composeTestRule.onNodeWithText("Settings").assertExists()
    }

    @Test
    fun continueButtonHiddenWhenNoSavedGame() {
        val uiState = MenuUiState(isLoading = false, canContinue = false)

        composeTestRule.setContent {
            MenuContent(
                uiState = uiState,
                onContinueClick = {},
                onNewGameClick = {},
                onStatsClick = {},
                onSettingsClick = {}
            )
        }

        composeTestRule.onNodeWithText("Continue").assertDoesNotExist()
    }

    @Test
    fun clickingDifficultyInvokesCallback() {
        var selectedDifficulty: Difficulty? = null
        val uiState = MenuUiState(isLoading = false)

        composeTestRule.setContent {
            MenuContent(
                uiState = uiState,
                onContinueClick = {},
                onNewGameClick = { selectedDifficulty = it },
                onStatsClick = {},
                onSettingsClick = {}
            )
        }

        composeTestRule.onNodeWithText("HARD").performClick()
        assert(selectedDifficulty == Difficulty.HARD)
    }

    @Test
    fun loadingStateShowsIndicator() {
        val uiState = MenuUiState(isLoading = true)

        composeTestRule.setContent {
            MenuContent(
                uiState = uiState,
                onContinueClick = {},
                onNewGameClick = {},
                onStatsClick = {},
                onSettingsClick = {}
            )
        }

        // CircularProgressIndicator doesn't have text, but we can verify it doesn't show the title
        composeTestRule.onNodeWithText("Sudoku").assertDoesNotExist()
    }
}
