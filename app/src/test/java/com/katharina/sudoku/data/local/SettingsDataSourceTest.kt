package com.katharina.sudoku.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.katharina.sudoku.domain.model.Difficulty
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
        
        assertThat(settings.isDarkMode).isNull()
        assertThat(settings.isSoundEnabled).isTrue()
        assertThat(settings.isHapticsEnabled).isTrue()
        assertThat(settings.defaultDifficulty).isEqualTo(Difficulty.EASY)
        assertThat(settings.highlightSameNumbers).isTrue()
        assertThat(settings.autoClearNotes).isTrue()
    }

    @Test
    fun `updateDarkMode updates setting`() = runBlocking {
        settingsDataSource.updateDarkMode(true)
        var settings = settingsDataSource.userSettings.first()
        assertThat(settings.isDarkMode).isTrue()

        settingsDataSource.updateDarkMode(null)
        settings = settingsDataSource.userSettings.first()
        assertThat(settings.isDarkMode).isNull()
    }

    @Test
    fun `updateDefaultDifficulty updates setting`() = runBlocking {
        settingsDataSource.updateDefaultDifficulty(Difficulty.EXPERT)
        val settings = settingsDataSource.userSettings.first()
        assertThat(settings.defaultDifficulty).isEqualTo(Difficulty.EXPERT)
    }

    @Test
    fun `updateSoundEnabled updates setting`() = runBlocking {
        settingsDataSource.updateSoundEnabled(false)
        val settings = settingsDataSource.userSettings.first()
        assertThat(settings.isSoundEnabled).isFalse()
    }
}
