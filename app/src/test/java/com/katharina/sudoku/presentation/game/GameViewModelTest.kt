package com.katharina.sudoku.presentation.game

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.katharina.sudoku.domain.model.Difficulty
import com.katharina.sudoku.domain.model.GameState
import com.katharina.sudoku.domain.model.Hint
import com.katharina.sudoku.domain.model.Position
import com.katharina.sudoku.domain.model.SudokuBoard
import com.katharina.sudoku.domain.repository.SudokuRepository
import com.katharina.sudoku.domain.usecase.CheckWinUseCase
import com.katharina.sudoku.domain.usecase.GenerateNewGameUseCase
import com.katharina.sudoku.domain.usecase.GetHintUseCase
import com.katharina.sudoku.domain.usecase.ValidateMoveUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {
    private val generateNewGameUseCase: GenerateNewGameUseCase = mockk()
    private val validateMoveUseCase: ValidateMoveUseCase = mockk()
    private val checkWinUseCase: CheckWinUseCase = mockk()
    private val getHintUseCase: GetHintUseCase = mockk()
    private val repository: SudokuRepository = mockk()
    private val savedStateHandle = SavedStateHandle()

    private lateinit var viewModel: GameViewModel
    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { generateNewGameUseCase(any()) } returns SudokuBoard.empty()
        every { repository.getGameState() } returns flowOf(null)
        every { repository.getStats() } returns flowOf(emptyList())
        coEvery { repository.saveGameState(any()) } returns Unit
        coEvery { repository.updateStats(any()) } returns Unit
        coEvery { repository.clearSavedGame() } returns Unit

        viewModel =
            GameViewModel(
                generateNewGameUseCase,
                validateMoveUseCase,
                checkWinUseCase,
                getHintUseCase,
                repository,
                testDispatcher,
                savedStateHandle,
            )
    }

    @AfterEach
    fun tearDown() {
        if (::viewModel.isInitialized) {
            viewModel.clearForTest()
        }
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() =
        runTest {
            try {
                viewModel.uiState.test {
                    val state = awaitItem()
                    assertThat(state.difficulty).isEqualTo(Difficulty.EASY)
                    assertThat(state.board).isEqualTo(SudokuBoard.empty())
                }
            } finally {
                viewModel.clearForTest()
            }
        }

    @Test
    fun `selecting a cell updates state`() =
        runTest {
            try {
                val position = Position(1, 1)
                viewModel.onCellSelected(position)

                viewModel.uiState.test {
                    val state = awaitItem()
                    assertThat(state.selectedPosition).isEqualTo(position)
                }
            } finally {
                viewModel.clearForTest()
            }
        }

    @Test
    fun `entering valid number updates board`() =
        runTest {
            try {
                val position = Position(0, 0)
                val value = 5
                every { validateMoveUseCase(any(), 0, 0, value) } returns true
                every { checkWinUseCase(any()) } returns false

                viewModel.onCellSelected(position)
                viewModel.onNumberInput(value)

                viewModel.uiState.test {
                    val state = awaitItem()
                    assertThat(state.board.getCell(position).value).isEqualTo(value)
                }
            } finally {
                viewModel.clearForTest()
            }
        }

    @Test
    fun `entering invalid number increments mistakes`() =
        runTest {
            try {
                val position = Position(0, 0)
                val value = 5
                every { validateMoveUseCase(any(), 0, 0, value) } returns false

                viewModel.onCellSelected(position)
                viewModel.onNumberInput(value)

                viewModel.uiState.test {
                    val state = awaitItem()
                    assertThat(state.mistakeCount).isEqualTo(1)
                    assertThat(state.board.getCell(position).value).isNull()
                }
            } finally {
                viewModel.clearForTest()
            }
        }

    @Test
    fun `toggling note mode updates state`() =
        runTest {
            try {
                viewModel.onToggleNoteMode()
                assertThat(viewModel.uiState.value.isNoteModeEnabled).isTrue()

                viewModel.onToggleNoteMode()
                assertThat(viewModel.uiState.value.isNoteModeEnabled).isFalse()
            } finally {
                viewModel.clearForTest()
            }
        }

    @Test
    fun `entering notes updates cell notes`() =
        runTest {
            try {
                val position = Position(0, 0)
                val value = 5
                viewModel.onCellSelected(position)
                viewModel.onToggleNoteMode()
                viewModel.onNumberInput(value)

                viewModel.uiState.test {
                    val state = awaitItem()
                    assertThat(state.board.getCell(position).notes).contains(value)
                }
            } finally {
                viewModel.clearForTest()
            }
        }

    @Test
    fun `undo reverts to previous board state`() =
        runTest {
            try {
                val position = Position(0, 0)
                val value = 5
                every { validateMoveUseCase(any(), 0, 0, value) } returns true
                every { checkWinUseCase(any()) } returns false

                viewModel.onCellSelected(position)
                viewModel.onNumberInput(value)
                assertThat(
                    viewModel.uiState.value.board
                        .getCell(position)
                        .value,
                ).isEqualTo(value)

                viewModel.onUndo()
                assertThat(
                    viewModel.uiState.value.board
                        .getCell(position)
                        .value,
                ).isNull()
            } finally {
                viewModel.clearForTest()
            }
        }

    @Test
    fun `redo reapplies reverted board state`() =
        runTest {
            try {
                val position = Position(0, 0)
                val value = 5
                every { validateMoveUseCase(any(), 0, 0, value) } returns true
                every { checkWinUseCase(any()) } returns false

                viewModel.onCellSelected(position)
                viewModel.onNumberInput(value)
                viewModel.onUndo()
                viewModel.onRedo()

                assertThat(
                    viewModel.uiState.value.board
                        .getCell(position)
                        .value,
                ).isEqualTo(value)
            } finally {
                viewModel.clearForTest()
            }
        }

    @Test
    fun `pausing game updates state`() =
        runTest {
            try {
                viewModel.onPauseResume()
                assertThat(viewModel.uiState.value.isPaused).isTrue()

                viewModel.onPauseResume()
                assertThat(viewModel.uiState.value.isPaused).isFalse()
            } finally {
                viewModel.clearForTest()
            }
        }

    @Test
    fun `timer increments over time`() =
        runTest {
            try {
                viewModel.uiState.test {
                    // Drop initial state
                    assertThat(awaitItem().timerSeconds).isEqualTo(0)

                    advanceTimeBy(1.seconds)
                    runCurrent()
                    assertThat(expectMostRecentItem().timerSeconds).isEqualTo(1)

                    advanceTimeBy(2.seconds)
                    runCurrent()
                    assertThat(expectMostRecentItem().timerSeconds).isEqualTo(3)
                }
            } finally {
                viewModel.clearForTest()
            }
        }

    @Test
    fun `requesting a hint updates board and moves selection`() =
        runTest {
            try {
                val hintPos = Position(2, 2)
                val hintVal = 9
                every { getHintUseCase(any()) } returns Hint(hintPos, hintVal, "Test hint")
                every { checkWinUseCase(any()) } returns false

                viewModel.onHintRequested()

                viewModel.uiState.test {
                    val state = awaitItem()
                    assertThat(state.selectedPosition).isEqualTo(hintPos)
                    assertThat(state.board.getCell(hintPos).value).isEqualTo(hintVal)
                }
            } finally {
                viewModel.clearForTest()
            }
        }

    @Test
    fun `game state is saved on number input`() =
        runTest {
            try {
                val position = Position(0, 0)
                val value = 5
                every { validateMoveUseCase(any(), 0, 0, value) } returns true
                every { checkWinUseCase(any()) } returns false

                viewModel.onCellSelected(position)
                viewModel.onNumberInput(value)
                runCurrent()

                coVerify { repository.saveGameState(any()) }
            } finally {
                viewModel.clearForTest()
            }
        }

    @Test
    fun `stats are updated when game is won`() =
        runTest {
            try {
                val position = Position(0, 0)
                val value = 5
                every { validateMoveUseCase(any(), 0, 0, value) } returns true
                every { checkWinUseCase(any()) } returns true
                every { repository.getStats() } returns flowOf(emptyList())

                viewModel.onCellSelected(position)
                viewModel.onNumberInput(value)
                runCurrent()

                coVerify { repository.updateStats(match { it.gamesWon == 1 }) }
            } finally {
                viewModel.clearForTest()
            }
        }

    @Test
    fun `restores state from repository on process death even with difficulty in route`() =
        runTest {
            tearDown()
            val savedBoard = SudokuBoard.empty().withUpdatedCell(Position(0, 0)) { it.copy(value = 9) }
            val savedGame = GameState(savedBoard, Difficulty.HARD, 100, 1)

            every { repository.getGameState() } returns flowOf(savedGame)

            // Simulate process death: is_initialized is true
            val restoredHandle =
                SavedStateHandle(
                    mapOf(
                        "difficulty" to Difficulty.HARD,
                        "is_initialized" to true,
                        "selected_row" to 1,
                        "selected_col" to 1,
                    ),
                )

            val restoredViewModel =
                GameViewModel(
                    generateNewGameUseCase,
                    validateMoveUseCase,
                    checkWinUseCase,
                    getHintUseCase,
                    repository,
                    testDispatcher,
                    restoredHandle,
                )

            try {
                restoredViewModel.uiState.test {
                    val state = awaitItem()
                    // Should use the saved game, not a new one
                    assertThat(state.board).isEqualTo(savedBoard)
                    assertThat(state.timerSeconds).isEqualTo(100)
                    assertThat(state.mistakeCount).isEqualTo(1)
                    assertThat(state.selectedPosition).isEqualTo(Position(1, 1))
                }

                restoredViewModel.clearForTest()
            } finally {
                // No need to clear main viewModel since we created a local one
            }
        }
}
