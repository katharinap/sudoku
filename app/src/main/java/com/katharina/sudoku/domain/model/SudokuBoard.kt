package com.katharina.sudoku.domain.model

data class SudokuBoard(
    val cells: List<Cell>
) {
    init {
        require(cells.size == 81) { "Sudoku board must have 81 cells" }
    }

    fun getCell(row: Int, col: Int): Cell {
        return cells[row * 9 + col]
    }

    fun getCell(position: Position): Cell {
        return getCell(position.row, position.column)
    }

    fun getRow(row: Int): List<Cell> {
        return (0..8).map { col -> getCell(row, col) }
    }

    fun getColumn(col: Int): List<Cell> {
        return (0..8).map { row -> getCell(row, col) }
    }

    fun getBox(boxIndex: Int): List<Cell> {
        require(boxIndex in 0..8) { "Box index must be between 0 and 8" }
        val startRow = (boxIndex / 3) * 3
        val startCol = (boxIndex % 3) * 3
        return (0..2).flatMap { r ->
            (0..2).map { c ->
                getCell(startRow + r, startCol + c)
            }
        }
    }

    fun getBoxIndex(row: Int, col: Int): Int {
        return (row / 3) * 3 + (col / 3)
    }

    companion object {
        fun empty(): SudokuBoard {
            val cells = (0..80).map { i ->
                Cell(Position(i / 9, i % 9))
            }
            return SudokuBoard(cells)
        }
    }
}
