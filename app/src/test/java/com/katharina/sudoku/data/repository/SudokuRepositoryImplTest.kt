package com.katharina.sudoku.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.katharina.sudoku.data.local.SudokuDao
import com.katharina.sudoku.data.local.SudokuDatabase
import com.katharina.sudoku.domain.model.Difficulty
import com.katharina.sudoku.domain.model.GameState
import com.katharina.sudoku.domain.model.GameStats
import com.katharina.sudoku.domain.model.SudokuBoard
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE, sdk = [34])
class SudokuRepositoryImplTest {

    private lateinit var database: SudokuDatabase
    private lateinit var dao: SudokuDao
    private lateinit var repository: SudokuRepositoryImpl

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SudokuDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.dao
        repository = SudokuRepositoryImpl(dao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `save and load game state`() = runBlocking {
        val gameState = GameState(
            board = SudokuBoard.empty(),
            difficulty = Difficulty.EASY,
            timerSeconds = 10,
            mistakes = 0
        )
        
        repository.saveGameState(gameState)
        val loaded = repository.getGameState().first()
        
        assertThat(loaded).isEqualTo(gameState)
    }

    @Test
    fun `clear saved game`() = runBlocking {
        val gameState = GameState(
            board = SudokuBoard.empty(),
            difficulty = Difficulty.EASY,
            timerSeconds = 10,
            mistakes = 0
        )
        
        repository.saveGameState(gameState)
        repository.clearSavedGame()
        val loaded = repository.getGameState().first()
        
        assertThat(loaded).isNull()
    }

    @Test
    fun `update and get stats`() = runBlocking {
        val stats = GameStats(
            difficulty = Difficulty.MEDIUM,
            gamesPlayed = 1,
            gamesWon = 1,
            bestTimeSeconds = 500
        )
        
        repository.updateStats(stats)
        val allStats = repository.getStats().first()
        
        assertThat(allStats).contains(stats)
    }
}
