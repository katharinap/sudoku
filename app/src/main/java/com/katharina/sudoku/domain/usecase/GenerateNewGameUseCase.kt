package com.katharina.sudoku.domain.usecase

import com.katharina.sudoku.domain.SudokuCarver
import com.katharina.sudoku.domain.SudokuGenerator
import com.katharina.sudoku.domain.model.Difficulty
import com.katharina.sudoku.domain.model.SudokuBoard
import javax.inject.Inject

class GenerateNewGameUseCase @Inject constructor() {
    operator fun invoke(difficulty: Difficulty): SudokuBoard {
        val solvedBoard = SudokuGenerator.generateSolvedBoard()
        return SudokuCarver.carve(solvedBoard, difficulty)
    }
}
