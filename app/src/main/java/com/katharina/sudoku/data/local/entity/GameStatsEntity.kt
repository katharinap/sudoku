package com.katharina.sudoku.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.katharina.sudoku.domain.model.Difficulty

@Entity(tableName = "game_stats")
data class GameStatsEntity(
    @PrimaryKey val difficulty: Difficulty,
    val gamesPlayed: Int,
    val gamesWon: Int,
    val bestTimeSeconds: Long
)
