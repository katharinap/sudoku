package com.katharina.sudoku.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.katharina.sudoku.domain.model.ThemeMode
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE, sdk = [34])
class SettingsDataSourceTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var settingsDataSource: SettingsDataSource

    @Before
    fun setUp() {
        dataStore = PreferenceDataStoreFactory.create(
            produceFile = { temporaryFolder.newFile("test_settings.preferences_pb") }
        )
        settingsDataSource = SettingsDataSource(dataStore)
    }

    @Test
    fun `initial settings are defaults`() = runBlocking {
        val settings = settingsDataSource.userSettings.first()
        
        assertThat(settings.themeMode).isEqualTo(ThemeMode.SYSTEM)
        assertThat(settings.highlightSameNumbers).isTrue()
        assertThat(settings.autoClearNotes).isTrue()
    }

    @Test
    fun `updateThemeMode updates setting`() = runBlocking {
        settingsDataSource.updateThemeMode(ThemeMode.DARK)
        var settings = settingsDataSource.userSettings.first()
        assertThat(settings.themeMode).isEqualTo(ThemeMode.DARK)

        settingsDataSource.updateThemeMode(ThemeMode.SYSTEM)
        settings = settingsDataSource.userSettings.first()
        assertThat(settings.themeMode).isEqualTo(ThemeMode.SYSTEM)
    }

    @Test
    fun `updateHighlightSameNumbers updates setting`() = runBlocking {
        settingsDataSource.updateHighlightSameNumbers(false)
        val settings = settingsDataSource.userSettings.first()
        assertThat(settings.highlightSameNumbers).isFalse()
    }
}
