package com.katharina.sudoku.presentation.settings

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.katharina.sudoku.domain.model.Difficulty
import com.katharina.sudoku.domain.model.ThemeMode
import com.katharina.sudoku.domain.model.UserSettings
import com.katharina.sudoku.domain.repository.SettingsRepository
import io.mockk.coEvery
import io.mockk.coVerify
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
class SettingsViewModelTest {

    private val repository: SettingsRepository = mockk()
    private lateinit var viewModel: SettingsViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val settingsFlow = MutableSharedFlow<UserSettings>()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { repository.userSettings } returns settingsFlow
        coEvery { repository.updateThemeMode(any()) } returns Unit
        coEvery { repository.updateDefaultDifficulty(any()) } returns Unit
        coEvery { repository.updateHighlightSameNumbers(any()) } returns Unit
        coEvery { repository.updateAutoClearNotes(any()) } returns Unit

        viewModel = SettingsViewModel(repository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `state updates when repository emits settings`() = runTest {
        viewModel.uiState.test {
            // Initial state from MutableStateFlow initialization
            var state = awaitItem()
            assertThat(state.isLoading).isTrue()

            // Emit settings
            val settings = UserSettings(highlightSameNumbers = false)
            settingsFlow.emit(settings)
            runCurrent()
            
            state = awaitItem()
            assertThat(state.isLoading).isFalse()
            assertThat(state.settings.highlightSameNumbers).isFalse()
        }
    }

    @Test
    fun `onThemeModeChanged calls repository`() = runTest {
        viewModel.onThemeModeChanged(ThemeMode.DARK)
        runCurrent()
        coVerify { repository.updateThemeMode(ThemeMode.DARK) }
    }

    @Test
    fun `onDefaultDifficultyChanged calls repository`() = runTest {
        viewModel.onDefaultDifficultyChanged(Difficulty.HARD)
        runCurrent()
        coVerify { repository.updateDefaultDifficulty(Difficulty.HARD) }
    }

    @Test
    fun `onHighlightSameNumbersChanged calls repository`() = runTest {
        viewModel.onHighlightSameNumbersChanged(false)
        runCurrent()
        coVerify { repository.updateHighlightSameNumbers(false) }
    }

    @Test
    fun `onAutoClearNotesChanged calls repository`() = runTest {
        viewModel.onAutoClearNotesChanged(false)
        runCurrent()
        coVerify { repository.updateAutoClearNotes(false) }
    }
}
