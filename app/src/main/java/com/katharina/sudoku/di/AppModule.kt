package com.katharina.sudoku.di

import com.katharina.sudoku.data.repository.DummyRepositoryImpl
import com.katharina.sudoku.domain.repository.DummyRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindDummyRepository(
        dummyRepositoryImpl: DummyRepositoryImpl
    ): DummyRepository
}
