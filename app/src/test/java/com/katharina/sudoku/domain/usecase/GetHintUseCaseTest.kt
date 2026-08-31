package com.katharina.sudoku.domain.usecase

import com.katharina.sudoku.domain.model.Position
import com.katharina.sudoku.domain.model.SudokuBoard
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class GetHintUseCaseTest {

    private val getHintUseCase = GetHintUseCase()

    @Test
    fun `invoke returns hint for a board with a naked single`() {
        // Create a board with a known naked single at (0,0)
        val cells = SudokuBoard.empty().cells.toMutableList()
        // Row 0: [?, 1, 2, 3, 4, 6, 7, 8, 9] -> only 5 left for (0,0)
        for (i in 1..8) {
            val value = if (i >= 5) i + 1 else i
            cells[i] = cells[i].copy(value = value)
        }
        val board = SudokuBoard(cells)
        
        val hint = getHintUseCase(board)
        
        assertThat(hint).isNotNull()
        assertThat(hint?.position).isEqualTo(Position(0, 0))
        assertThat(hint?.value).isEqualTo(5)
    }
}
