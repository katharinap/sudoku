package com.katharina.sudoku.data.mapper

import com.katharina.sudoku.data.local.entity.GameStateEntity
import com.katharina.sudoku.domain.model.GameState
import com.katharina.sudoku.domain.model.SudokuBoard
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun GameStateEntity.toDomain(): GameState {
    return GameState(
        board = Json.decodeFromString(boardJson),
        difficulty = difficulty,
        timerSeconds = timerSeconds,
        mistakes = mistakes
    )
}

fun GameState.toEntity(id: Int = 0): GameStateEntity {
    return GameStateEntity(
        id = id,
        boardJson = Json.encodeToString(board),
        difficulty = difficulty,
        timerSeconds = timerSeconds,
        mistakes = mistakes
    )
}
