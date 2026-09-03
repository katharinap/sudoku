package com.katharina.sudoku.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.katharina.sudoku.domain.model.Difficulty
import com.katharina.sudoku.domain.repository.SudokuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val repository: SudokuRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    init {
        combine(
            repository.getGameState(),
            repository.getStats()
        ) { gameState, stats ->
            _uiState.update {
                it.copy(
                    canContinue = gameState != null,
                    stats = stats,
                    isLoading = false
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onNewGame(difficulty: Difficulty) {
        // This will be handled by navigation to GameScreen with arguments
        // and GameViewModel will trigger startNewGame(difficulty)
    }

    fun onContinueGame() {
        // This will be handled by navigation to GameScreen
    }
}
