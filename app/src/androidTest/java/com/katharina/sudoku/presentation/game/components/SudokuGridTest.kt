package com.katharina.sudoku.presentation.game.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.katharina.sudoku.domain.model.Position
import com.katharina.sudoku.domain.model.SudokuBoard
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SudokuGridTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun clickingCellInvokesCallbackWithCorrectPosition() {
        var clickedPosition: Position? = null
        val board = SudokuBoard.empty().withUpdatedCell(Position(0, 0)) {
            it.copy(value = 5)
        }

        composeTestRule.setContent {
            SudokuGrid(
                board = board,
                selectedPosition = null,
                onCellClick = { clickedPosition = it }
            )
        }

        // We use the text "5" to find the cell at (0,0)
        composeTestRule.onNodeWithText("5").performClick()

        assertEquals(Position(0, 0), clickedPosition)
    }

    @Test
    fun notesAreDisplayedWhenValueIsNull() {
        val board = SudokuBoard.empty().withUpdatedCell(Position(0, 0)) {
            it.copy(notes = setOf(1, 2, 9))
        }

        composeTestRule.setContent {
            SudokuGrid(
                board = board,
                selectedPosition = null,
                onCellClick = {}
            )
        }

        composeTestRule.onNodeWithText("1").assertExists()
        composeTestRule.onNodeWithText("2").assertExists()
        composeTestRule.onNodeWithText("9").assertExists()
    }
}
