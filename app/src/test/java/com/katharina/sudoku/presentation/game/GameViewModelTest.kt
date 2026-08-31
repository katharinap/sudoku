package com.katharina.sudoku.presentation.game

import app.cash.turbine.test
import com.katharina.sudoku.domain.model.Difficulty
import com.katharina.sudoku.domain.model.Position
import com.katharina.sudoku.domain.model.SudokuBoard
import com.katharina.sudoku.domain.usecase.CheckWinUseCase
import com.katharina.sudoku.domain.usecase.GenerateNewGameUseCase
import com.katharina.sudoku.domain.usecase.ValidateMoveUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    private val generateNewGameUseCase: GenerateNewGameUseCase = mockk()
    private val validateMoveUseCase: ValidateMoveUseCase = mockk()
    private val checkWinUseCase: CheckWinUseCase = mockk()

    private lateinit var viewModel: GameViewModel
    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { generateNewGameUseCase(any()) } returns SudokuBoard.empty()
        viewModel = GameViewModel(generateNewGameUseCase, validateMoveUseCase, checkWinUseCase)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.difficulty).isEqualTo(Difficulty.EASY)
            assertThat(state.board).isEqualTo(SudokuBoard.empty())
        }
    }

    @Test
    fun `selecting a cell updates state`() = runTest {
        val position = Position(1, 1)
        viewModel.onCellSelected(position)

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.selectedPosition).isEqualTo(position)
        }
    }

    @Test
    fun `entering valid number updates board`() = runTest {
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
    }

    @Test
    fun `entering invalid number increments mistakes`() = runTest {
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
    }

    @Test
    fun `toggling note mode updates state`() = runTest {
        viewModel.onToggleNoteMode()
        assertThat(viewModel.uiState.value.isNoteModeEnabled).isTrue()

        viewModel.onToggleNoteMode()
        assertThat(viewModel.uiState.value.isNoteModeEnabled).isFalse()
    }

    @Test
    fun `entering notes updates cell notes`() = runTest {
        val position = Position(0, 0)
        val value = 5
        viewModel.onCellSelected(position)
        viewModel.onToggleNoteMode()
        viewModel.onNumberInput(value)

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.board.getCell(position).notes).contains(value)
        }
    }

    @Test
    fun `undo reverts to previous board state`() = runTest {
        val position = Position(0, 0)
        val value = 5
        every { validateMoveUseCase(any(), 0, 0, value) } returns true
        every { checkWinUseCase(any()) } returns false

        viewModel.onCellSelected(position)
        viewModel.onNumberInput(value)
        assertThat(viewModel.uiState.value.board.getCell(position).value).isEqualTo(value)

        viewModel.onUndo()
        assertThat(viewModel.uiState.value.board.getCell(position).value).isNull()
    }

    @Test
    fun `redo reapplies reverted board state`() = runTest {
        val position = Position(0, 0)
        val value = 5
        every { validateMoveUseCase(any(), 0, 0, value) } returns true
        every { checkWinUseCase(any()) } returns false

        viewModel.onCellSelected(position)
        viewModel.onNumberInput(value)
        viewModel.onUndo()
        viewModel.onRedo()

        assertThat(viewModel.uiState.value.board.getCell(position).value).isEqualTo(value)
    }

    @Test
    fun `pausing game updates state`() = runTest {
        viewModel.onPauseResume()
        assertThat(viewModel.uiState.value.isPaused).isTrue()

        viewModel.onPauseResume()
        assertThat(viewModel.uiState.value.isPaused).isFalse()
    }
}
