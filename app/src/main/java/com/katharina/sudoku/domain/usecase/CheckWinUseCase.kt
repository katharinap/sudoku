package com.katharina.sudoku.domain.usecase

import com.katharina.sudoku.domain.SudokuValidator
import com.katharina.sudoku.domain.model.SudokuBoard
import javax.inject.Inject

class CheckWinUseCase @Inject constructor() {
    operator fun invoke(board: SudokuBoard): Boolean {
        return SudokuValidator.isBoardComplete(board)
    }
}
