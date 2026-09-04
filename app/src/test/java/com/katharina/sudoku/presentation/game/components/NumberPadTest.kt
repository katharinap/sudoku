package com.katharina.sudoku.presentation.game.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class NumberPadTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun clickingNumberInvokesCallback() {
        var clickedNumber: Int? = null
        composeTestRule.setContent {
            NumberPad(
                onNumberClick = { clickedNumber = it },
                onEraseClick = {}
            )
        }

        composeTestRule.onNodeWithText("5").performClick()
        assertEquals(5, clickedNumber)
    }

    @Test
    fun clickingEraseInvokesCallback() {
        var eraseClicked = false
        composeTestRule.setContent {
            NumberPad(
                onNumberClick = {},
                onEraseClick = { eraseClicked = true }
            )
        }

        composeTestRule.onNodeWithText("⌫").performClick()
        assert(eraseClicked)
    }
}
