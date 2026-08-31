package com.katharina.sudoku.data.repository

import com.katharina.sudoku.data.local.SudokuDao
import com.katharina.sudoku.data.mapper.toDomain
import com.katharina.sudoku.data.mapper.toEntity
import com.katharina.sudoku.domain.model.GameState
import com.katharina.sudoku.domain.model.GameStats
import com.katharina.sudoku.domain.repository.SudokuRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SudokuRepositoryImpl @Inject constructor(
    private val dao: SudokuDao
) : SudokuRepository {

    override fun getGameState(): Flow<GameState?> {
        return dao.getGameState().map { it?.toDomain() }
    }

    override suspend fun saveGameState(gameState: GameState) {
        dao.insertGameState(gameState.toEntity())
    }

    override suspend fun clearSavedGame() {
        dao.deleteGameState()
    }

    override fun getStats(): Flow<List<GameStats>> {
        return dao.getStats().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun updateStats(stats: GameStats) {
        dao.updateStats(stats.toEntity())
    }
}
