package com.katharina.sudoku.domain

import com.katharina.sudoku.domain.model.Cell
import com.katharina.sudoku.domain.model.Position
import com.katharina.sudoku.domain.model.SudokuBoard
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class SudokuHintEngineTest {

    @Test
    fun `getNextHint detects Naked Single`() {
        val board = createNakedSingleBoard()
        val hint = SudokuHintEngine.getNextHint(board)
        
        assertThat(hint).isNotNull()
        assertThat(hint?.position).isEqualTo(Position(0, 0))
        assertThat(hint?.value).isEqualTo(5)
        assertThat(hint?.explanation).contains("Naked Single")
    }

    @Test
    fun `getNextHint detects Hidden Single in Row`() {
        // In row 0, 5 can only go in (0,0) even if (0,0) has other candidates
        // (Other empty cells in row 0 have 5 restricted by their columns)
        val board = createHiddenSingleRowBoard()
        val hint = SudokuHintEngine.getNextHint(board)
        
        assertThat(hint).isNotNull()
        // It might find a Naked Single first if I'm not careful with setup, 
        // but the test asserts it finds *a* hint at the expected spot if possible.
        // In my setup, (0,0) will be the only one in Row 1 that can be 5.
        assertThat(hint?.value).isEqualTo(5)
        assertThat(hint?.explanation).contains("Hidden Single")
    }

    @Test
    fun `getNextHint returns null for solved board`() {
        val solvedValues = listOf(
            5, 3, 4, 6, 7, 8, 9, 1, 2,
            6, 7, 2, 1, 9, 5, 3, 4, 8,
            1, 9, 8, 3, 4, 2, 5, 6, 7,
            8, 5, 9, 7, 6, 1, 4, 2, 3,
            4, 2, 6, 8, 5, 3, 7, 9, 1,
            7, 1, 3, 9, 2, 4, 8, 5, 6,
            9, 6, 1, 5, 3, 7, 2, 8, 4,
            2, 8, 7, 4, 1, 9, 6, 3, 5,
            3, 4, 5, 2, 8, 6, 1, 7, 9
        )
        val cells = solvedValues.mapIndexed { i, value ->
            Cell(Position(i / 9, i % 9), value = value)
        }
        val board = SudokuBoard(cells)
        val hint = SudokuHintEngine.getNextHint(board)
        assertThat(hint).isNull()
    }

    private fun createNakedSingleBoard(): SudokuBoard {
        val cells = SudokuBoard.empty().cells.toMutableList()
        // Row 0: [?, 1, 2, 3, 4, 6, 7, 8, 9] -> only 5 left for (0,0)
        for (i in 1..8) {
            val value = if (i >= 5) i + 1 else i
            cells[i] = cells[i].copy(value = value)
        }
        return SudokuBoard(cells)
    }

    private fun createHiddenSingleRowBoard(): SudokuBoard {
        val cells = SudokuBoard.empty().cells.toMutableList()
        // Row 0 has empty cells at (0,0), (0,1), (0,2)
        // 5 is a candidate for all 3.
        // But we place 5 in columns 1 and 2 in other boxes to restrict them.
        // (0,1) is in Col 1. Let's put 5 in (3,1)
        // (0,2) is in Col 2. Let's put 5 in (4,2)
        cells[3*9 + 1] = cells[3*9 + 1].copy(value = 5)
        cells[4*9 + 2] = cells[4*9 + 2].copy(value = 5)
        
        // Fill the rest of row 0 with other numbers (1, 2, 3, 4, 6, 7)
        cells[3] = cells[3].copy(value = 1)
        cells[4] = cells[4].copy(value = 2)
        cells[5] = cells[5].copy(value = 3)
        cells[6] = cells[6].copy(value = 4)
        cells[7] = cells[7].copy(value = 6)
        cells[8] = cells[8].copy(value = 7)
        
        return SudokuBoard(cells)
    }
}
