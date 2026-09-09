package com.katharina.sudoku.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SudokuBoardTest {

    @Test
    fun `empty board has 81 cells with correct positions and null values`() {
        val board = SudokuBoard.empty()
        assertThat(board.cells).hasSize(81)
        
        for (row in 0..8) {
            for (col in 0..8) {
                val cell = board.getCell(row, col)
                assertThat(cell.position).isEqualTo(Position(row, col))
                assertThat(cell.value).isNull()
                assertThat(cell.isFixed).isFalse()
                assertThat(cell.notes).isEmpty()
            }
        }
    }

    @Test
    fun `getRow returns correct cells`() {
        val board = SudokuBoard.empty()
        val rowIdx = 5
        val row = board.getRow(rowIdx)
        
        assertThat(row).hasSize(9)
        row.forEachIndexed { col, cell ->
            assertThat(cell.position).isEqualTo(Position(rowIdx, col))
        }
    }

    @Test
    fun `getColumn returns correct cells`() {
        val board = SudokuBoard.empty()
        val colIdx = 3
        val column = board.getColumn(colIdx)
        
        assertThat(column).hasSize(9)
        column.forEachIndexed { row, cell ->
            assertThat(cell.position).isEqualTo(Position(row, colIdx))
        }
    }

    @Test
    fun `getBox returns correct cells for each box`() {
        val board = SudokuBoard.empty()
        
        // Box 0 (Top-left)
        val box0 = board.getBox(0)
        assertThat(box0).hasSize(9)
        assertThat(box0.map { it.position }).containsExactly(
            Position(0, 0), Position(0, 1), Position(0, 2),
            Position(1, 0), Position(1, 1), Position(1, 2),
            Position(2, 0), Position(2, 1), Position(2, 2)
        ).inOrder()

        // Box 4 (Center)
        val box4 = board.getBox(4)
        assertThat(box4).hasSize(9)
        assertThat(box4.map { it.position }).containsExactly(
            Position(3, 3), Position(3, 4), Position(3, 5),
            Position(4, 3), Position(4, 4), Position(4, 5),
            Position(5, 3), Position(5, 4), Position(5, 5)
        ).inOrder()
        
        // Box 8 (Bottom-right)
        val box8 = board.getBox(8)
        assertThat(box8).hasSize(9)
        assertThat(box8.map { it.position }).containsExactly(
            Position(6, 6), Position(6, 7), Position(6, 8),
            Position(7, 6), Position(7, 7), Position(7, 8),
            Position(8, 6), Position(8, 7), Position(8, 8)
        ).inOrder()
    }

    @Test
    fun `getBoxIndex returns correct box index for positions`() {
        val board = SudokuBoard.empty()
        
        assertThat(board.getBoxIndex(0, 0)).isEqualTo(0)
        assertThat(board.getBoxIndex(2, 2)).isEqualTo(0)
        assertThat(board.getBoxIndex(0, 3)).isEqualTo(1)
        assertThat(board.getBoxIndex(3, 0)).isEqualTo(3)
        assertThat(board.getBoxIndex(4, 4)).isEqualTo(4)
        assertThat(board.getBoxIndex(8, 8)).isEqualTo(8)
    }

    @Test
    fun `Position throws exception for invalid coordinates`() {
        assertThrows<IllegalArgumentException> { Position(-1, 0) }
        assertThrows<IllegalArgumentException> { Position(0, 9) }
    }

    @Test
    fun `Cell throws exception for invalid value`() {
        assertThrows<IllegalArgumentException> { Cell(Position(0, 0), value = 10, solutionValue = 1) }
        assertThrows<IllegalArgumentException> { Cell(Position(0, 0), value = 0, solutionValue = 1) }
    }
}
