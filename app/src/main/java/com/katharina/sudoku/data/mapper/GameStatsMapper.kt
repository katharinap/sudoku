package com.katharina.sudoku.data.mapper

import com.katharina.sudoku.data.local.entity.GameStatsEntity
import com.katharina.sudoku.domain.model.GameStats

fun GameStatsEntity.toDomain(): GameStats {
    return GameStats(
        difficulty = difficulty,
        gamesPlayed = gamesPlayed,
        gamesWon = gamesWon,
        bestTimeSeconds = bestTimeSeconds
    )
}

fun GameStats.toEntity(): GameStatsEntity {
    return GameStatsEntity(
        difficulty = difficulty,
        gamesPlayed = gamesPlayed,
        gamesWon = gamesWon,
        bestTimeSeconds = bestTimeSeconds
    )
}
