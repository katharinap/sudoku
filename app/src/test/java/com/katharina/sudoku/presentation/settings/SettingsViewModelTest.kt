package com.katharina.sudoku.presentation.settings

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.katharina.sudoku.data.local.SettingsDataSource
import com.katharina.sudoku.domain.model.Difficulty
import com.katharina.sudoku.domain.model.UserSettings
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

    private val dataSource: SettingsDataSource = mockk()
    private lateinit var viewModel: SettingsViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val settingsFlow = MutableSharedFlow<UserSettings>()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { dataSource.userSettings } returns settingsFlow
        coEvery { dataSource.updateDarkMode(any()) } returns Unit
        coEvery { dataSource.updateSoundEnabled(any()) } returns Unit
        coEvery { dataSource.updateHapticsEnabled(any()) } returns Unit
        coEvery { dataSource.updateDefaultDifficulty(any()) } returns Unit
        coEvery { dataSource.updateHighlightSameNumbers(any()) } returns Unit
        coEvery { dataSource.updateAutoClearNotes(any()) } returns Unit

        viewModel = SettingsViewModel(dataSource)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `state updates when dataSource emits settings`() = runTest {
        viewModel.uiState.test {
            // Initial state from MutableStateFlow initialization
            var state = awaitItem()
            assertThat(state.isLoading).isTrue()

            // Emit settings
            val settings = UserSettings(isSoundEnabled = false)
            settingsFlow.emit(settings)
            runCurrent()
            
            state = awaitItem()
            assertThat(state.isLoading).isFalse()
            assertThat(state.settings.isSoundEnabled).isFalse()
        }
    }

    @Test
    fun `onDarkModeChanged calls dataSource`() = runTest {
        viewModel.onDarkModeChanged(true)
        runCurrent()
        coVerify { dataSource.updateDarkMode(true) }
    }

    @Test
    fun `onSoundChanged calls dataSource`() = runTest {
        viewModel.onSoundChanged(false)
        runCurrent()
        coVerify { dataSource.updateSoundEnabled(false) }
    }

    @Test
    fun `onHapticsChanged calls dataSource`() = runTest {
        viewModel.onHapticsChanged(false)
        runCurrent()
        coVerify { dataSource.updateHapticsEnabled(false) }
    }

    @Test
    fun `onDefaultDifficultyChanged calls dataSource`() = runTest {
        viewModel.onDefaultDifficultyChanged(Difficulty.HARD)
        runCurrent()
        coVerify { dataSource.updateDefaultDifficulty(Difficulty.HARD) }
    }

    @Test
    fun `onHighlightSameNumbersChanged calls dataSource`() = runTest {
        viewModel.onHighlightSameNumbersChanged(false)
        runCurrent()
        coVerify { dataSource.updateHighlightSameNumbers(false) }
    }

    @Test
    fun `onAutoClearNotesChanged calls dataSource`() = runTest {
        viewModel.onAutoClearNotesChanged(false)
        runCurrent()
        coVerify { dataSource.updateAutoClearNotes(false) }
    }
}
