package com.katharina.sudoku

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun appTitle_isDisplayed() {
        composeTestRule.onNodeWithText("Sudoku").assertExists()
    }
}
