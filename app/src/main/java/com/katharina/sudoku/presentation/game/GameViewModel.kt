package com.katharina.sudoku.presentation.game

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.katharina.sudoku.di.DefaultDispatcher
import com.katharina.sudoku.domain.model.Difficulty
import com.katharina.sudoku.domain.model.GameState
import com.katharina.sudoku.domain.model.Position
import com.katharina.sudoku.domain.model.SudokuBoard
import com.katharina.sudoku.domain.repository.SudokuRepository
import com.katharina.sudoku.domain.usecase.CheckWinUseCase
import com.katharina.sudoku.domain.usecase.GenerateNewGameUseCase
import com.katharina.sudoku.domain.usecase.GetHintUseCase
import com.katharina.sudoku.domain.usecase.ValidateMoveUseCase
import com.katharina.sudoku.presentation.navigation.GameRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.navigation.toRoute
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class GameViewModel
    @Inject
    constructor(
        private val generateNewGameUseCase: GenerateNewGameUseCase,
        private val validateMoveUseCase: ValidateMoveUseCase,
        private val checkWinUseCase: CheckWinUseCase,
        private val getHintUseCase: GetHintUseCase,
        private val repository: SudokuRepository,
        @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
        savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(GameUiState())
        val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

        private val undoStack = java.util.ArrayDeque<SudokuBoard>()
        private val redoStack = java.util.ArrayDeque<SudokuBoard>()
        private var timerJob: Job? = null

        init {
            val difficultyArg = try {
                savedStateHandle.toRoute<GameRoute>().difficulty
            } catch (e: Exception) {
                null
            }
            
            viewModelScope.launch {
                val savedGame = repository.getGameState().first()
                
                if (difficultyArg != null) {
                    // Force new game if difficulty is provided in route
                    startNewGame(difficultyArg)
                } else if (savedGame != null) {
                    // Continue existing game
                    _uiState.update {
                        it.copy(
                            board = savedGame.board,
                            difficulty = savedGame.difficulty,
                            timerSeconds = savedGame.timerSeconds,
                            mistakeCount = savedGame.mistakes,
                        )
                    }
                    startTimer()
                } else {
                    // Fallback
                    startNewGame(Difficulty.EASY)
                }
            }
        }

        fun startNewGame(difficulty: Difficulty) {
            val board = generateNewGameUseCase(difficulty)
            _uiState.update {
                GameUiState(
                    board = board,
                    difficulty = difficulty,
                )
            }
            undoStack.clear()
            redoStack.clear()
            startTimer()
            saveGame()
        }

        fun onCellSelected(position: Position) {
            if (_uiState.value.isPaused || _uiState.value.isComplete) return
            _uiState.update { it.copy(selectedPosition = position) }
        }

        fun onNumberInput(value: Int) {
            val state = _uiState.value
            if (state.isPaused || state.isComplete) return
            val position = state.selectedPosition ?: return
            val cell = state.board.getCell(position)
            if (cell.isFixed) return

            if (state.isNoteModeEnabled) {
                updateNotes(position, value)
            } else {
                updateValue(position, value)
            }
            saveGame()
        }

        fun onEraseInput() {
            val state = _uiState.value
            if (state.isPaused || state.isComplete) return
            val position = state.selectedPosition ?: return
            val cell = state.board.getCell(position)
            if (cell.isFixed || cell.value == null && cell.notes.isEmpty()) return

            pushToUndoStack(state.board)
            val newBoard =
                state.board.withUpdatedCell(position) {
                    it.copy(value = null, notes = emptySet())
                }
            _uiState.update { it.copy(board = newBoard) }
            saveGame()
        }

        fun onToggleNoteMode() {
            _uiState.update { it.copy(isNoteModeEnabled = !it.isNoteModeEnabled) }
        }

        fun onHintRequested() {
            val state = _uiState.value
            if (state.isPaused || state.isComplete) return

            getHintUseCase(state.board)?.let { hint ->
                pushToUndoStack(state.board)
                val newBoard =
                    state.board.withUpdatedCell(hint.position) {
                        it.copy(value = hint.value, notes = emptySet())
                    }
                val isComplete = checkWinUseCase(newBoard)
                _uiState.update {
                    it.copy(
                        board = newBoard,
                        selectedPosition = hint.position,
                        isComplete = isComplete,
                    )
                }
                saveGame()
            }
        }

        fun onUndo() {
            if (undoStack.isEmpty() || _uiState.value.isPaused || _uiState.value.isComplete) return

            val currentBoard = _uiState.value.board
            redoStack.push(currentBoard)

            val previousBoard = undoStack.pop()
            _uiState.update { it.copy(board = previousBoard) }
            saveGame()
        }

        fun onRedo() {
            if (redoStack.isEmpty() || _uiState.value.isPaused || _uiState.value.isComplete) return

            val currentBoard = _uiState.value.board
            undoStack.push(currentBoard)

            val nextBoard = redoStack.pop()
            _uiState.update { it.copy(board = nextBoard) }
            saveGame()
        }

        fun onPauseResume() {
            if (_uiState.value.isComplete) return
            _uiState.update { it.copy(isPaused = !it.isPaused) }
        }

        private fun startTimer() {
            timerJob?.cancel()
            timerJob =
                viewModelScope.launch(defaultDispatcher) {
                    while (isActive) {
                        delay(1.seconds)
                        val state = _uiState.value
                        if (!state.isPaused && !state.isComplete) {
                            _uiState.update { it.copy(timerSeconds = it.timerSeconds + 1) }
                            if (_uiState.value.timerSeconds % 10 == 0L) {
                                saveGame()
                            }
                        }
                    }
                }
        }

        override fun onCleared() {
            super.onCleared()
            timerJob?.cancel()
        }

        internal fun clearForTest() {
            onCleared()
        }

        private fun saveGame() {
            val state = _uiState.value
            if (state.isComplete) {
                viewModelScope.launch {
                    repository.clearSavedGame()
                }
            } else {
                viewModelScope.launch {
                    repository.saveGameState(
                        GameState(
                            board = state.board,
                            difficulty = state.difficulty,
                            timerSeconds = state.timerSeconds,
                            mistakes = state.mistakeCount,
                        ),
                    )
                }
            }
        }

        private fun updateValue(
            position: Position,
            value: Int,
        ) {
            val currentBoard = _uiState.value.board

            if (validateMoveUseCase(currentBoard, position.row, position.column, value)) {
                pushToUndoStack(currentBoard)
                val newBoard =
                    currentBoard.withUpdatedCell(position) {
                        it.copy(value = value, notes = emptySet())
                    }
                val isComplete = checkWinUseCase(newBoard)
                _uiState.update {
                    it.copy(board = newBoard, isComplete = isComplete)
                }
            } else {
                _uiState.update {
                    it.copy(
                        mistakeCount = it.mistakeCount + 1,
                        errorPosition = position
                    )
                }
                viewModelScope.launch {
                    delay(500.milliseconds)
                    if (_uiState.value.errorPosition == position) {
                        _uiState.update { it.copy(errorPosition = null) }
                    }
                }
            }
        }

        private fun updateNotes(
            position: Position,
            value: Int,
        ) {
            val currentBoard = _uiState.value.board
            pushToUndoStack(currentBoard)

            val newBoard =
                currentBoard.withUpdatedCell(position) { cell ->
                    val newNotes =
                        if (cell.notes.contains(value)) {
                            cell.notes - value
                        } else {
                            cell.notes + value
                        }
                    cell.copy(notes = newNotes, value = null)
                }
            _uiState.update { it.copy(board = newBoard) }
        }

        private fun pushToUndoStack(board: SudokuBoard) {
            undoStack.push(board)
            redoStack.clear()
        }
    }
