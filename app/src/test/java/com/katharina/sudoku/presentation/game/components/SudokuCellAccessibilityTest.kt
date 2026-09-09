package com.katharina.sudoku.presentation.game.components

import androidx.compose.ui.test.assertContentDescriptionContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import com.katharina.sudoku.domain.model.Cell
import com.katharina.sudoku.domain.model.Position
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SudokuCellAccessibilityTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun sudokuCell_verifyContentDescription_fixedValue() {
        val cell = Cell(Position(0, 0), value = 5, solutionValue = 5, isFixed = true)
        
        composeTestRule.setContent {
            SudokuCell(
                cell = cell,
                isSelected = false,
                isPeerHighlighted = false,
                isSameNumberHighlighted = false,
                isError = false,
                onClick = {}
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Fixed Value 5 at row 1 column 1", substring = true)
            .assertExists()
    }

    @Test
    fun sudokuCell_verifyContentDescription_notes() {
        val cell = Cell(Position(1, 2), notes = setOf(1, 4, 9), solutionValue = 5)
        
        composeTestRule.setContent {
            SudokuCell(
                cell = cell,
                isSelected = false,
                isPeerHighlighted = false,
                isSameNumberHighlighted = false,
                isError = false,
                onClick = {}
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Notes 1, 4, 9 at row 2 column 3", substring = true)
            .assertExists()
    }
}
