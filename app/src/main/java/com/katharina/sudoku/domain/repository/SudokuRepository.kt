package com.katharina.sudoku.domain.repository

import com.katharina.sudoku.domain.model.GameState
import com.katharina.sudoku.domain.model.GameStats
import kotlinx.coroutines.flow.Flow

interface SudokuRepository {
    fun getGameState(): Flow<GameState?>
    suspend fun saveGameState(gameState: GameState)
    suspend fun clearSavedGame()
    
    fun getStats(): Flow<List<GameStats>>
    suspend fun updateStats(stats: GameStats)
}
