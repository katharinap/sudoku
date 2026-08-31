package com.katharina.sudoku.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.katharina.sudoku.domain.model.Difficulty

@Entity(tableName = "game_state")
data class GameStateEntity(
    @PrimaryKey val id: Int = 0, // Only one active game at a time
    val boardJson: String,
    val difficulty: Difficulty,
    val timerSeconds: Long,
    val mistakes: Int
)
