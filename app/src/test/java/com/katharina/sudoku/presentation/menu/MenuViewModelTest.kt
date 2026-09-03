package com.katharina.sudoku.presentation.menu

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.katharina.sudoku.domain.model.Difficulty
import com.katharina.sudoku.domain.model.GameState
import com.katharina.sudoku.domain.model.GameStats
import com.katharina.sudoku.domain.repository.SudokuRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MenuViewModelTest {

    private val repository: SudokuRepository = mockk()
    private lateinit var viewModel: MenuViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val gameStateFlow = MutableStateFlow<GameState?>(null)
    private val statsFlow = MutableStateFlow<List<GameStats>>(emptyList())

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { repository.getGameState() } returns gameStateFlow
        every { repository.getStats() } returns statsFlow

        viewModel = MenuViewModel(repository)
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
    fun `state updates when repository emits game state`() = runTest {
        viewModel.uiState.test {
            // Initial state
            var state = awaitItem()
            assertThat(state.isLoading).isTrue()
            assertThat(state.canContinue).isFalse()

            // Emit game state
            val mockGameState = mockk<GameState>()
            gameStateFlow.value = mockGameState
            
            state = awaitItem()
            assertThat(state.isLoading).isFalse()
            assertThat(state.canContinue).isTrue()
        }
    }

    @Test
    fun `state updates when repository emits stats`() = runTest {
        viewModel.uiState.test {
            awaitItem() // Initial

            val stats = listOf(
                GameStats(Difficulty.EASY, 10, 5, 100L)
            )
            statsFlow.value = stats

            val state = awaitItem()
            assertThat(state.stats).isEqualTo(stats)
            assertThat(state.isLoading).isFalse()
        }
    }
}
