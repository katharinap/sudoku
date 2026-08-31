package com.katharina.sudoku.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.katharina.sudoku.data.local.entity.GameStateEntity
import com.katharina.sudoku.data.local.entity.GameStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SudokuDao {
    @Query("SELECT * FROM game_state WHERE id = 0")
    fun getGameState(): Flow<GameStateEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameState(gameState: GameStateEntity)

    @Query("DELETE FROM game_state WHERE id = 0")
    suspend fun deleteGameState()

    @Query("SELECT * FROM game_stats")
    fun getStats(): Flow<List<GameStatsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateStats(stats: GameStatsEntity)
}
