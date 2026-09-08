package com.katharina.sudoku.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.katharina.sudoku.data.local.SettingsDataSource
import com.katharina.sudoku.data.local.SudokuDao
import com.katharina.sudoku.data.local.SudokuDatabase
import com.katharina.sudoku.data.repository.SettingsRepositoryImpl
import com.katharina.sudoku.data.repository.SudokuRepositoryImpl
import com.katharina.sudoku.domain.repository.SettingsRepository
import com.katharina.sudoku.domain.repository.SudokuRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSudokuDatabase(@ApplicationContext context: Context): SudokuDatabase {
        return Room.databaseBuilder(
            context,
            SudokuDatabase::class.java,
            "sudoku_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideSudokuDao(database: SudokuDatabase): SudokuDao {
        return database.dao
    }

    @Provides
    @Singleton
    fun provideSudokuRepository(dao: SudokuDao): SudokuRepository {
        return SudokuRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(dataSource: SettingsDataSource): SettingsRepository {
        return SettingsRepositoryImpl(dataSource)
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("settings") }
        )
    }
}
