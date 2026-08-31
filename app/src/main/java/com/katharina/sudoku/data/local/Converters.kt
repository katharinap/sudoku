package com.katharina.sudoku.data.local

import androidx.room.TypeConverter
import com.katharina.sudoku.domain.model.Difficulty

class Converters {
    @TypeConverter
    fun fromDifficulty(difficulty: Difficulty): String {
        return difficulty.name
    }

    @TypeConverter
    fun toDifficulty(name: String): Difficulty {
        return Difficulty.valueOf(name)
    }
}
