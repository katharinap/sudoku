package com.katharina.sudoku.data.repository

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.katharina.sudoku.data.local.SudokuDao
import com.katharina.sudoku.data.local.entity.GameStateEntity
import com.katharina.sudoku.domain.model.Difficulty
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SudokuRepositoryTest {

    private lateinit var repository: SudokuRepositoryImpl
    private val dao: SudokuDao = mockk()

    @Before
    fun setUp() {
        repository = SudokuRepositoryImpl(dao)
    }

    @Test
    fun `getGameState returns null on corrupted JSON and clears database`() = runTest {
        val corruptedEntity = GameStateEntity(
            id = 0,
            boardJson = "{ corrupted json }",
            difficulty = Difficulty.EASY,
            timerSeconds = 0,
            mistakes = 0
        )
        every { dao.getGameState() } returns flowOf(corruptedEntity)
        coEvery { dao.deleteGameState() } returns Unit

        repository.getGameState().test {
            val result = awaitItem()
            assertThat(result).isNull()
            awaitComplete()
        }

        coVerify { dao.deleteGameState() }
    }

    @Test
    fun `getGameState returns null when no game is saved`() = runTest {
        every { dao.getGameState() } returns flowOf(null)

        repository.getGameState().test {
            val result = awaitItem()
            assertThat(result).isNull()
            awaitComplete()
        }
    }
}
