package com.katharina.sudoku.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.katharina.sudoku.data.local.entity.GameStateEntity
import com.katharina.sudoku.data.local.entity.GameStatsEntity
import com.katharina.sudoku.domain.model.Difficulty
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
class SudokuDaoTest {

    private lateinit var database: SudokuDatabase
    private lateinit var dao: SudokuDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SudokuDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.dao
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetGameState() = runBlocking {
        val gameState = GameStateEntity(
            boardJson = "{}",
            difficulty = Difficulty.EASY,
            timerSeconds = 120,
            mistakes = 1
        )
        dao.insertGameState(gameState)

        val retrieved = dao.getGameState().first()
        assertThat(retrieved).isEqualTo(gameState)
    }

    @Test
    fun deleteGameState() = runBlocking {
        val gameState = GameStateEntity(
            boardJson = "{}",
            difficulty = Difficulty.EASY,
            timerSeconds = 120,
            mistakes = 1
        )
        dao.insertGameState(gameState)
        dao.deleteGameState()

        val retrieved = dao.getGameState().first()
        assertThat(retrieved).isNull()
    }

    @Test
    fun updateAndGetStats() = runBlocking {
        val stats = GameStatsEntity(
            difficulty = Difficulty.HARD,
            gamesPlayed = 10,
            gamesWon = 5,
            bestTimeSeconds = 300
        )
        dao.updateStats(stats)

        val retrieved = dao.getStats().first()
        assertThat(retrieved).contains(stats)
    }
}
