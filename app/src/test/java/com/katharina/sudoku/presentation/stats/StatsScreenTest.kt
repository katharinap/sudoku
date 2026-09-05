package com.katharina.sudoku.presentation.stats

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.katharina.sudoku.domain.model.Difficulty
import com.katharina.sudoku.domain.model.GameStats
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class StatsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun statsScreenRendersCorrectlyWithData() {
        val mockStats = mapOf(
            Difficulty.EASY to GameStats(Difficulty.EASY, 10, 8, 45L)
        )
        val uiState = StatsUiState(statsByDifficulty = mockStats, isLoading = false)

        composeTestRule.setContent {
            StatsContent(
                uiState = uiState,
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithText("Statistics").assertIsDisplayed()
        composeTestRule.onNodeWithText("EASY").assertIsDisplayed()
        composeTestRule.onNodeWithText("10").assertIsDisplayed() // Played
        composeTestRule.onNodeWithText("8 (80%)").assertIsDisplayed() // Won
        composeTestRule.onNodeWithText("00:45").assertIsDisplayed() // Best Time
    }

    @Test
    fun statsScreenShowsEmptyMessageWhenNoStats() {
        val uiState = StatsUiState(statsByDifficulty = emptyMap(), isLoading = false)

        composeTestRule.setContent {
            StatsContent(
                uiState = uiState,
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithText("No games played yet").assertIsDisplayed()
    }

    @Test
    fun statsScreenShowsLoadingIndicator() {
        val uiState = StatsUiState(isLoading = true)

        composeTestRule.setContent {
            StatsContent(
                uiState = uiState,
                onBackClick = {}
            )
        }

        // Title should be visible, but cards/empty message should not
        composeTestRule.onNodeWithText("Statistics").assertIsDisplayed()
        composeTestRule.onNodeWithText("EASY").assertDoesNotExist()
        composeTestRule.onNodeWithText("No games played yet").assertDoesNotExist()
    }
}
