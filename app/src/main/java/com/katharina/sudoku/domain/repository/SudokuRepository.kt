package com.katharina.sudoku.domain.repository

import com.katharina.sudoku.domain.model.SudokuBoard
import kotlinx.coroutines.flow.Flow

interface SudokuRepository {
    fun getGameState(): Flow<SudokuBoard?>
    suspend fun saveGameState(board: SudokuBoard)
    suspend fun clearSavedGame()
}
