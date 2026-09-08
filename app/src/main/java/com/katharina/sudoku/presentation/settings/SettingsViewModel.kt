package com.katharina.sudoku.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.katharina.sudoku.domain.model.Difficulty
import com.katharina.sudoku.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        repository.userSettings
            .onEach { settings ->
                _uiState.update {
                    it.copy(
                        settings = settings,
                        isLoading = false
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onDarkModeChanged(isDarkMode: Boolean?) {
        viewModelScope.launch {
            repository.updateDarkMode(isDarkMode)
        }
    }

    fun onSoundChanged(isEnabled: Boolean) {
        viewModelScope.launch {
            repository.updateSoundEnabled(isEnabled)
        }
    }

    fun onHapticsChanged(isEnabled: Boolean) {
        viewModelScope.launch {
            repository.updateHapticsEnabled(isEnabled)
        }
    }

    fun onDefaultDifficultyChanged(difficulty: Difficulty) {
        viewModelScope.launch {
            repository.updateDefaultDifficulty(difficulty)
        }
    }

    fun onHighlightSameNumbersChanged(isEnabled: Boolean) {
        viewModelScope.launch {
            repository.updateHighlightSameNumbers(isEnabled)
        }
    }

    fun onAutoClearNotesChanged(isEnabled: Boolean) {
        viewModelScope.launch {
            repository.updateAutoClearNotes(isEnabled)
        }
    }
}
