package com.katharina.sudoku.presentation.stats

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.katharina.sudoku.domain.model.Difficulty
import com.katharina.sudoku.domain.model.GameStats
import com.katharina.sudoku.domain.repository.SudokuRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StatsViewModelTest {

    private val repository: SudokuRepository = mockk()
    private lateinit var viewModel: StatsViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val statsFlow = MutableSharedFlow<List<GameStats>>()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { repository.getStats() } returns statsFlow
        viewModel = StatsViewModel(repository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has isLoading true`() = runTest {
        assertThat(viewModel.uiState.value.isLoading).isTrue()
    }

    @Test
    fun `state updates when repository emits stats`() = runTest {
        viewModel.uiState.test {
            // Initial state
            var state = awaitItem()
            assertThat(state.isLoading).isTrue()
            assertThat(state.statsByDifficulty).isEmpty()

            // Emit stats
            val stats = listOf(
                GameStats(Difficulty.EASY, 5, 3, 120L),
                GameStats(Difficulty.MEDIUM, 2, 1, 300L)
            )
            statsFlow.emit(stats)
            runCurrent()

            state = awaitItem()
            assertThat(state.isLoading).isFalse()
            assertThat(state.statsByDifficulty).hasSize(2)
            assertThat(state.statsByDifficulty[Difficulty.EASY]).isEqualTo(stats[0])
            assertThat(state.statsByDifficulty[Difficulty.MEDIUM]).isEqualTo(stats[1])
        }
    }

    @Test
    fun `state updates correctly when stats are updated`() = runTest {
        viewModel.uiState.test {
            awaitItem() // Initial

            val initialStats = listOf(GameStats(Difficulty.EASY, 1, 1, 100L))
            statsFlow.emit(initialStats)
            runCurrent()
            awaitItem()

            val updatedStats = listOf(GameStats(Difficulty.EASY, 2, 2, 90L))
            statsFlow.emit(updatedStats)
            runCurrent()
            
            val state = awaitItem()
            assertThat(state.statsByDifficulty[Difficulty.EASY]?.gamesPlayed).isEqualTo(2)
            assertThat(state.statsByDifficulty[Difficulty.EASY]?.bestTimeSeconds).isEqualTo(90L)
        }
    }
}
