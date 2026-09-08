package com.katharina.sudoku.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.katharina.sudoku.R
import com.katharina.sudoku.domain.model.Difficulty
import com.katharina.sudoku.domain.model.ThemeMode
import com.katharina.sudoku.ui.theme.SudokuTheme

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    SettingsContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onThemeModeChanged = viewModel::onThemeModeChanged,
        onSoundChanged = viewModel::onSoundChanged,
        onHapticsChanged = viewModel::onHapticsChanged,
        onDefaultDifficultyChanged = viewModel::onDefaultDifficultyChanged,
        onHighlightSameNumbersChanged = viewModel::onHighlightSameNumbersChanged,
        onAutoClearNotesChanged = viewModel::onAutoClearNotesChanged,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    uiState: SettingsUiState,
    onBackClick: () -> Unit,
    onThemeModeChanged: (ThemeMode) -> Unit,
    onSoundChanged: (Boolean) -> Unit,
    onHapticsChanged: (Boolean) -> Unit,
    onDefaultDifficultyChanged: (Difficulty) -> Unit,
    onHighlightSameNumbersChanged: (Boolean) -> Unit,
    onAutoClearNotesChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                SettingsGroup(title = "Appearance") {
                    ThemeModeDropdown(
                        currentThemeMode = uiState.settings.themeMode,
                        onThemeModeSelected = onThemeModeChanged
                    )
                }

                HorizontalDivider()

                SettingsGroup(title = "Game") {
                    ListItem(
                        headlineContent = { Text("Sound Effects") },
                        trailingContent = {
                            Switch(
                                checked = uiState.settings.isSoundEnabled,
                                onCheckedChange = onSoundChanged
                            )
                        }
                    )
                    ListItem(
                        headlineContent = { Text("Haptic Feedback") },
                        trailingContent = {
                            Switch(
                                checked = uiState.settings.isHapticsEnabled,
                                onCheckedChange = onHapticsChanged
                            )
                        }
                    )
                    ListItem(
                        headlineContent = { Text("Highlight Same Numbers") },
                        trailingContent = {
                            Switch(
                                checked = uiState.settings.highlightSameNumbers,
                                onCheckedChange = onHighlightSameNumbersChanged
                            )
                        }
                    )
                    ListItem(
                        headlineContent = { Text("Auto Clear Notes") },
                        trailingContent = {
                            Switch(
                                checked = uiState.settings.autoClearNotes,
                                onCheckedChange = onAutoClearNotesChanged
                            )
                        }
                    )
                }

                HorizontalDivider()

                SettingsGroup(title = "Defaults") {
                    DifficultyDropdown(
                        currentDifficulty = uiState.settings.defaultDifficulty,
                        onDifficultySelected = onDefaultDifficultyChanged
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsGroup(
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        content()
    }
}

@Composable
private fun ThemeModeDropdown(
    currentThemeMode: ThemeMode,
    onThemeModeSelected: (ThemeMode) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        ListItem(
            headlineContent = { Text("App Theme") },
            supportingContent = { 
                Text(when(currentThemeMode) {
                    ThemeMode.LIGHT -> "Light"
                    ThemeMode.DARK -> "Dark"
                    ThemeMode.SYSTEM -> "System Default"
                })
            },
            modifier = Modifier.clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ThemeMode.entries.forEach { mode ->
                DropdownMenuItem(
                    text = { 
                        Text(when(mode) {
                            ThemeMode.LIGHT -> "Light"
                            ThemeMode.DARK -> "Dark"
                            ThemeMode.SYSTEM -> "System Default"
                        })
                    },
                    onClick = {
                        onThemeModeSelected(mode)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun DifficultyDropdown(
    currentDifficulty: Difficulty,
    onDifficultySelected: (Difficulty) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        ListItem(
            headlineContent = { Text("Default Difficulty") },
            supportingContent = { Text(currentDifficulty.name) },
            modifier = Modifier.clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Difficulty.entries.forEach { difficulty ->
                DropdownMenuItem(
                    text = { Text(difficulty.name) },
                    onClick = {
                        onDifficultySelected(difficulty)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SudokuTheme {
        SettingsContent(
            uiState = SettingsUiState(isLoading = false),
            onBackClick = {},
            onThemeModeChanged = {},
            onSoundChanged = {},
            onHapticsChanged = {},
            onDefaultDifficultyChanged = {},
            onHighlightSameNumbersChanged = {},
            onAutoClearNotesChanged = {}
        )
    }
}
