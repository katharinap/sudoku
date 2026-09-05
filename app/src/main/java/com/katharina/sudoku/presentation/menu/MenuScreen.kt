package com.katharina.sudoku.presentation.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.katharina.sudoku.domain.model.Difficulty
import com.katharina.sudoku.ui.theme.SudokuTheme

@Composable
fun MenuScreen(
    viewModel: MenuViewModel,
    onContinueClick: () -> Unit,
    onNewGameClick: (Difficulty) -> Unit,
    onStatsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    MenuContent(
        uiState = uiState,
        onContinueClick = onContinueClick,
        onNewGameClick = onNewGameClick,
        onStatsClick = onStatsClick,
        onSettingsClick = onSettingsClick,
        modifier = modifier
    )
}

@Composable
fun MenuContent(
    uiState: MenuUiState,
    onContinueClick: () -> Unit,
    onNewGameClick: (Difficulty) -> Unit,
    onStatsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Sudoku",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(48.dp))

                if (uiState.canContinue) {
                    MenuButton(
                        text = "Continue",
                        onClick = onContinueClick,
                        isPrimary = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Text(
                    text = "New Game",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Difficulty.entries.forEach { difficulty ->
                    MenuButton(
                        text = difficulty.name,
                        onClick = { onNewGameClick(difficulty) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.widthIn(max = 300.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onStatsClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Stats")
                    }
                    OutlinedButton(
                        onClick = onSettingsClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Settings")
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false
) {
    if (isPrimary) {
        Button(
            onClick = onClick,
            modifier = modifier.widthIn(min = 200.dp, max = 300.dp).fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = text, style = MaterialTheme.typography.titleMedium)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.widthIn(min = 200.dp, max = 300.dp).fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = text, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuScreenPreviewFirstRun() {
    SudokuTheme {
        MenuContent(
            uiState = MenuUiState(canContinue = false, isLoading = false),
            onContinueClick = {},
            onNewGameClick = {},
            onStatsClick = {},
            onSettingsClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MenuScreenPreviewResuming() {
    SudokuTheme {
        MenuContent(
            uiState = MenuUiState(canContinue = true, isLoading = false),
            onContinueClick = {},
            onNewGameClick = {},
            onStatsClick = {},
            onSettingsClick = {}
        )
    }
}
