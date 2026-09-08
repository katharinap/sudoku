package com.katharina.sudoku.presentation.game

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.katharina.sudoku.di.DefaultDispatcher
import com.katharina.sudoku.domain.SudokuValidator
import com.katharina.sudoku.domain.model.Difficulty
import com.katharina.sudoku.domain.model.GameState
import com.katharina.sudoku.domain.model.GameStats
import com.katharina.sudoku.domain.model.Position
import com.katharina.sudoku.domain.model.SudokuBoard
import com.katharina.sudoku.domain.model.UserSettings
import com.katharina.sudoku.domain.repository.SettingsRepository
import com.katharina.sudoku.domain.repository.SudokuRepository
import com.katharina.sudoku.domain.usecase.CheckWinUseCase
import com.katharina.sudoku.domain.usecase.GenerateNewGameUseCase
import com.katharina.sudoku.domain.usecase.GetHintUseCase
import com.katharina.sudoku.domain.usecase.ValidateMoveUseCase
import com.katharina.sudoku.presentation.navigation.GameRoute
import dagger.hilt.android.lifecycle.HiltViewModel
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
        private val settingsRepository: SettingsRepository,
        @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
        private val savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(GameUiState())
        val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

        private var userSettings = UserSettings()

        private val undoStack = java.util.ArrayDeque<SudokuBoard>()
        private val redoStack = java.util.ArrayDeque<SudokuBoard>()
        private var timerJob: Job? = null

        init {
            val isInitialized = savedStateHandle.get<Boolean>("is_initialized") ?: false
            val difficultyArg =
                try {
                    savedStateHandle.toRoute<GameRoute>().difficulty
                } catch (e: Exception) {
                    null
                }

            viewModelScope.launch {
                val savedGame = repository.getGameState().first()

                if (!isInitialized && difficultyArg != null) {
                    // Fresh navigation with difficulty -> Start new game
                    startNewGame(difficultyArg)
                    savedStateHandle["is_initialized"] = true
                } else if (savedGame != null) {
                    // Restoring from process death OR continuing from menu
                    val savedRow = savedStateHandle.get<Int>("selected_row")
                    val savedCol = savedStateHandle.get<Int>("selected_col")
                    val restoredPosition =
                        if (savedRow != null && savedCol != null) {
                            Position(savedRow, savedCol)
                        } else {
                            null
                        }

                    _uiState.update {
                        it.copy(
                            board = savedGame.board,
                            difficulty = savedGame.difficulty,
                            timerSeconds = savedGame.timerSeconds,
                            mistakeCount = savedGame.mistakes,
                            selectedPosition = restoredPosition,
                        )
                    }
                    savedStateHandle["is_initialized"] = true
                    startTimer()
                } else {
                    // Fallback
                    startNewGame(Difficulty.EASY)
                    savedStateHandle["is_initialized"] = true
                }
            }

            viewModelScope.launch {
                settingsRepository.userSettings.collect { settings ->
                    userSettings = settings
                    _uiState.update { it.copy(highlightSameNumbers = settings.highlightSameNumbers) }
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
            savedStateHandle["selected_position"] = null as Position?
            undoStack.clear()
            redoStack.clear()
            savedStateHandle["selected_row"] = null as Int?
            savedStateHandle["selected_col"] = null as Int?
            startTimer()
            saveGame()

            viewModelScope.launch {
                val allStats = repository.getStats().first()
                val currentStats =
                    allStats.find { it.difficulty == difficulty }
                        ?: GameStats(difficulty, 0, 0, 0)
                repository.updateStats(currentStats.copy(gamesPlayed = currentStats.gamesPlayed + 1))
            }
        }

        fun onCellSelected(position: Position) {
            if (_uiState.value.isPaused || _uiState.value.isComplete) return
            _uiState.update { it.copy(selectedPosition = position) }
            savedStateHandle["selected_row"] = position.row
            savedStateHandle["selected_col"] = position.column
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
            if (cell.isFixed || ((cell.value == null) && cell.notes.isEmpty())) return

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

            val hint = getHintUseCase(state.board)
            if (hint != null) {
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
                if (isComplete) {
                    onGameWon()
                }
            } else {
                _uiState.update { it.copy(message = "No hints available") }
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

        fun onResetGame() {
            val currentState = _uiState.value
            if (currentState.isComplete) return

            val originalBoard =
                SudokuBoard(
                    cells =
                        currentState.board.cells.map { cell ->
                            if (cell.isFixed) cell else cell.copy(value = null, notes = emptySet())
                        },
                )

            _uiState.update {
                it.copy(
                    board = originalBoard,
                    timerSeconds = 0,
                    mistakeCount = 0,
                    selectedPosition = null,
                    errorPosition = null,
                    conflictPositions = emptySet(),
                )
            }
            undoStack.clear()
            redoStack.clear()
            savedStateHandle["selected_row"] = null as Int?
            savedStateHandle["selected_col"] = null as Int?
            saveGame()
        }

        fun onValidateBoard() {
            val currentState = _uiState.value
            if (currentState.isComplete || currentState.isPaused) return

            val conflicts = SudokuValidator.findConflicts(currentState.board).toSet()
            if (conflicts.isNotEmpty()) {
                _uiState.update { it.copy(conflictPositions = conflicts) }
                viewModelScope.launch(defaultDispatcher) {
                    delay(2000.milliseconds)
                    _uiState.update { it.copy(conflictPositions = emptySet()) }
                }
            } else {
                _uiState.update { it.copy(message = "No conflicts found") }
            }
        }

        fun onDismissMessage() {
            _uiState.update { it.copy(message = null) }
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

        private fun onGameWon() {
            val state = _uiState.value
            viewModelScope.launch {
                val allStats = repository.getStats().first()
                val currentStats =
                    allStats.find { it.difficulty == state.difficulty }
                        ?: GameStats(state.difficulty, 0, 0, 0L)

                val newStats =
                    currentStats.copy(
                        gamesWon = currentStats.gamesWon + 1,
                        bestTimeSeconds =
                            if (currentStats.bestTimeSeconds == 0L || state.timerSeconds < currentStats.bestTimeSeconds) {
                                state.timerSeconds
                            } else {
                                currentStats.bestTimeSeconds
                            },
                    )
                repository.updateStats(newStats)
            }
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
                var newBoard =
                    currentBoard.withUpdatedCell(position) {
                        it.copy(value = value, notes = emptySet())
                    }
                
                if (userSettings.autoClearNotes) {
                    val boxIndex = currentBoard.getBoxIndex(position.row, position.column)
                    newBoard = SudokuBoard(
                        cells = newBoard.cells.map { cell ->
                            val isPeer = cell.position.row == position.row ||
                                    cell.position.column == position.column ||
                                    currentBoard.getBoxIndex(cell.position.row, cell.position.column) == boxIndex
                            
                            if (isPeer && cell.position != position) {
                                cell.copy(notes = cell.notes - value)
                            } else {
                                cell
                            }
                        }
                    )
                }

                val isComplete = checkWinUseCase(newBoard)
                _uiState.update {
                    it.copy(board = newBoard, isComplete = isComplete)
                }
                if (isComplete) {
                    onGameWon()
                }
            } else {
                _uiState.update {
                    it.copy(
                        mistakeCount = it.mistakeCount + 1,
                        errorPosition = position,
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
