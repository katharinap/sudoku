package com.katharina.sudoku.presentation

import androidx.lifecycle.ViewModel
import com.katharina.sudoku.domain.repository.DummyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DummyViewModel @Inject constructor(
    private val repository: DummyRepository
) : ViewModel() {
    fun getMessage(): String = repository.getMessage()
}
