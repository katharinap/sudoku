package com.katharina.sudoku.presentation

import com.katharina.sudoku.domain.repository.DummyRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DummyViewModelTest {

    private lateinit var viewModel: DummyViewModel
    private val repository: DummyRepository = mockk()

    @BeforeEach
    fun setUp() {
        viewModel = DummyViewModel(repository)
    }

    @Test
    fun `getMessage returns message from repository`() {
        // Given
        val expectedMessage = "Test Message"
        every { repository.getMessage() } returns expectedMessage

        // When
        val actualMessage = viewModel.getMessage()

        // Then
        assertThat(actualMessage).isEqualTo(expectedMessage)
    }
}
