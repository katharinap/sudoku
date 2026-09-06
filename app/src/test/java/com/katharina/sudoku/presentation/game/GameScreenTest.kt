package com.katharina.sudoku.presentation.game

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.katharina.sudoku.domain.model.Difficulty
import com.katharina.sudoku.domain.model.Position
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class GameScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun gameScreenRendersCorrectly() {
        val uiState = GameUiState(
            difficulty = Difficulty.MEDIUM,
            timerSeconds = 65,
            mistakeCount = 1
        )

        composeTestRule.setContent {
            GameContent(
                uiState = uiState,
                onCellClick = {},
                onNumberClick = {},
                onUndoClick = {},
                onRedoClick = {},
                onHintClick = {},
                onToggleNoteMode = {},
                onResetClick = {},
                onCheckClick = {},
                onPauseResumeClick = {},
                onEraseClick = {},
                onPlayAgain = {},
                onBackToMenu = {},
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithText("MEDIUM").assertExists()
        composeTestRule.onNodeWithText("01:05").assertExists()
        composeTestRule.onNodeWithText("Mistakes: 1/3").assertExists()
    }

    @Test
    fun clickingNumberInvokesCallback() {
        var clickedNumber: Int? = null
        val uiState = GameUiState(
            selectedPosition = Position(0, 0)
        )

        composeTestRule.setContent {
            GameContent(
                uiState = uiState,
                onCellClick = {},
                onNumberClick = { clickedNumber = it },
                onUndoClick = {},
                onRedoClick = {},
                onHintClick = {},
                onToggleNoteMode = {},
                onResetClick = {},
                onCheckClick = {},
                onPauseResumeClick = {},
                onEraseClick = {},
                onPlayAgain = {},
                onBackToMenu = {},
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithText("5").performClick()
        assert(clickedNumber == 5)
    }

    @Test
    fun clickingPauseInvokesCallback() {
        var pauseClicked = false
        val uiState = GameUiState(isPaused = false)

        composeTestRule.setContent {
            GameContent(
                uiState = uiState,
                onCellClick = {},
                onNumberClick = {},
                onUndoClick = {},
                onRedoClick = {},
                onHintClick = {},
                onToggleNoteMode = {},
                onResetClick = {},
                onCheckClick = {},
                onPauseResumeClick = { pauseClicked = true },
                onEraseClick = {},
                onPlayAgain = {},
                onBackToMenu = {},
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Pause").performClick()
        assert(pauseClicked)
    }

    @Test
    fun pauseOverlayIsVisibleWhenPaused() {
        val uiState = GameUiState(isPaused = true)

        composeTestRule.setContent {
            GameContent(
                uiState = uiState,
                onCellClick = {},
                onNumberClick = {},
                onUndoClick = {},
                onRedoClick = {},
                onHintClick = {},
                onToggleNoteMode = {},
                onResetClick = {},
                onCheckClick = {},
                onPauseResumeClick = {},
                onEraseClick = {},
                onPlayAgain = {},
                onBackToMenu = {},
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithText("Game Paused").assertIsDisplayed()
    }

    @Test
    fun winDialogIsVisibleWhenComplete() {
        val uiState = GameUiState(isComplete = true)

        composeTestRule.setContent {
            GameContent(
                uiState = uiState,
                onCellClick = {},
                onNumberClick = {},
                onUndoClick = {},
                onRedoClick = {},
                onHintClick = {},
                onToggleNoteMode = {},
                onResetClick = {},
                onCheckClick = {},
                onPauseResumeClick = {},
                onEraseClick = {},
                onPlayAgain = {},
                onBackToMenu = {},
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithText("Congratulations!").assertExists()
    }
}
