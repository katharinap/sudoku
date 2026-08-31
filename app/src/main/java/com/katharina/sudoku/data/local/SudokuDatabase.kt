package com.katharina.sudoku.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.katharina.sudoku.data.local.entity.GameStateEntity
import com.katharina.sudoku.data.local.entity.GameStatsEntity

@Database(
    entities = [GameStateEntity::class, GameStatsEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SudokuDatabase : RoomDatabase() {
    abstract val dao: SudokuDao
}
