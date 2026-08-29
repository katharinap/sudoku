package com.katharina.sudoku.data.repository

import com.katharina.sudoku.domain.repository.DummyRepository
import javax.inject.Inject

class DummyRepositoryImpl @Inject constructor() : DummyRepository {
    override fun getMessage(): String = "Hello from Hilt!"
}
