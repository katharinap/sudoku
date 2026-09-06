package com.katharina.sudoku.presentation.game.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class GameControlsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun undoClickInvokesCallback() {
        var undoClicked = false
        composeTestRule.setContent {
            GameControls(
                onUndoClick = { undoClicked = true },
                onRedoClick = {},
                onHintClick = {},
                onToggleNoteMode = {},
                onResetClick = {},
                onCheckClick = {},
                isNoteModeEnabled = false
            )
        }

        composeTestRule.onNodeWithContentDescription("Undo").performClick()
        assert(undoClicked)
    }

    @Test
    fun redoClickInvokesCallback() {
        var redoClicked = false
        composeTestRule.setContent {
            GameControls(
                onUndoClick = {},
                onRedoClick = { redoClicked = true },
                onHintClick = {},
                onToggleNoteMode = {},
                onResetClick = {},
                onCheckClick = {},
                isNoteModeEnabled = false
            )
        }

        composeTestRule.onNodeWithContentDescription("Redo").performClick()
        assert(redoClicked)
    }

    @Test
    fun hintClickInvokesCallback() {
        var hintClicked = false
        composeTestRule.setContent {
            GameControls(
                onUndoClick = {},
                onRedoClick = {},
                onHintClick = { hintClicked = true },
                onToggleNoteMode = {},
                onResetClick = {},
                onCheckClick = {},
                isNoteModeEnabled = false
            )
        }

        composeTestRule.onNodeWithContentDescription("Hint").performClick()
        assert(hintClicked)
    }

    @Test
    fun noteModeToggleInvokesCallback() {
        var noteModeToggled = false
        composeTestRule.setContent {
            GameControls(
                onUndoClick = {},
                onRedoClick = {},
                onHintClick = {},
                onToggleNoteMode = { noteModeToggled = true },
                onResetClick = {},
                onCheckClick = {},
                isNoteModeEnabled = false
            )
        }

        composeTestRule.onNodeWithContentDescription("Notes").performClick()
        assert(noteModeToggled)
    }

    @Test
    fun resetClickInvokesCallback() {
        var resetClicked = false
        composeTestRule.setContent {
            GameControls(
                onUndoClick = {},
                onRedoClick = {},
                onHintClick = {},
                onToggleNoteMode = {},
                onResetClick = { resetClicked = true },
                onCheckClick = {},
                isNoteModeEnabled = false
            )
        }

        composeTestRule.onNodeWithContentDescription("Reset").performClick()
        assert(resetClicked)
    }

    @Test
    fun checkClickInvokesCallback() {
        var checkClicked = false
        composeTestRule.setContent {
            GameControls(
                onUndoClick = {},
                onRedoClick = {},
                onHintClick = {},
                onToggleNoteMode = {},
                onResetClick = {},
                onCheckClick = { checkClicked = true },
                isNoteModeEnabled = false
            )
        }

        composeTestRule.onNodeWithContentDescription("Check").performClick()
        assert(checkClicked)
    }
}
