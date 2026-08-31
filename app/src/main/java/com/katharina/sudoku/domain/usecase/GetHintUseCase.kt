package com.katharina.sudoku.domain.usecase

import com.katharina.sudoku.domain.SudokuHintEngine
import com.katharina.sudoku.domain.model.Hint
import com.katharina.sudoku.domain.model.SudokuBoard
import javax.inject.Inject

class GetHintUseCase @Inject constructor() {
    operator fun invoke(board: SudokuBoard): Hint? {
        return SudokuHintEngine.getNextHint(board)
    }
}
