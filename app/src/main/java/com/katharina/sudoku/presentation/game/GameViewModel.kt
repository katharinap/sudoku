package com.katharina.sudoku.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.katharina.sudoku.domain.model.Difficulty
import com.katharina.sudoku.domain.model.Position
import com.katharina.sudoku.domain.model.SudokuBoard
import com.katharina.sudoku.domain.usecase.CheckWinUseCase
import com.katharina.sudoku.domain.usecase.GenerateNewGameUseCase
import com.katharina.sudoku.domain.usecase.ValidateMoveUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val generateNewGameUseCase: GenerateNewGameUseCase,
    private val validateMoveUseCase: ValidateMoveUseCase,
    private val checkWinUseCase: CheckWinUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val undoStack = java.util.ArrayDeque<SudokuBoard>()
    private val redoStack = java.util.ArrayDeque<SudokuBoard>()

    init {
        startNewGame(Difficulty.EASY)
    }

    fun startNewGame(difficulty: Difficulty) {
        val board = generateNewGameUseCase(difficulty)
        _uiState.update {
            GameUiState(
                board = board,
                difficulty = difficulty
            )
        }
        undoStack.clear()
        redoStack.clear()
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
    }

    fun onToggleNoteMode() {
        _uiState.update { it.copy(isNoteModeEnabled = !it.isNoteModeEnabled) }
    }

    fun onUndo() {
        if (undoStack.isEmpty() || _uiState.value.isPaused || _uiState.value.isComplete) return
        
        val currentBoard = _uiState.value.board
        redoStack.push(currentBoard)
        
        val previousBoard = undoStack.pop()
        _uiState.update { it.copy(board = previousBoard) }
    }

    fun onRedo() {
        if (redoStack.isEmpty() || _uiState.value.isPaused || _uiState.value.isComplete) return
        
        val currentBoard = _uiState.value.board
        undoStack.push(currentBoard)
        
        val nextBoard = redoStack.pop()
        _uiState.update { it.copy(board = nextBoard) }
    }

    fun onPauseResume() {
        if (_uiState.value.isComplete) return
        _uiState.update { it.copy(isPaused = !it.isPaused) }
    }

    private fun updateValue(position: Position, value: Int) {
        val currentBoard = _uiState.value.board
        
        if (validateMoveUseCase(currentBoard, position.row, position.column, value)) {
            pushToUndoStack(currentBoard)
            val newBoard = currentBoard.withUpdatedCell(position) { 
                it.copy(value = value, notes = emptySet()) 
            }
            val isComplete = checkWinUseCase(newBoard)
            _uiState.update { 
                it.copy(board = newBoard, isComplete = isComplete) 
            }
        } else {
            _uiState.update { it.copy(mistakeCount = it.mistakeCount + 1) }
        }
    }

    private fun updateNotes(position: Position, value: Int) {
        val currentBoard = _uiState.value.board
        pushToUndoStack(currentBoard)
        
        val newBoard = currentBoard.withUpdatedCell(position) { cell ->
            val newNotes = if (cell.notes.contains(value)) {
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
        // Limit stack size if needed, but 81 cells * reasonable depth is fine for now
    }
}
