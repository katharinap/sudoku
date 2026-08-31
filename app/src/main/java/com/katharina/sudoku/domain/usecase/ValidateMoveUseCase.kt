package com.katharina.sudoku.domain.usecase

import com.katharina.sudoku.domain.SudokuValidator
import com.katharina.sudoku.domain.model.SudokuBoard
import javax.inject.Inject

class ValidateMoveUseCase @Inject constructor() {
    operator fun invoke(board: SudokuBoard, row: Int, col: Int, value: Int): Boolean {
        return SudokuValidator.isValidPlacement(board, row, col, value)
    }
}
