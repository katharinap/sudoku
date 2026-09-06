package com.katharina.sudoku.presentation.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.katharina.sudoku.R
import com.katharina.sudoku.domain.model.Position
import com.katharina.sudoku.presentation.game.components.GameControls
import com.katharina.sudoku.presentation.game.components.NumberPad
import com.katharina.sudoku.presentation.game.components.PauseOverlay
import com.katharina.sudoku.presentation.game.components.SudokuGrid
import com.katharina.sudoku.presentation.game.components.WinDialog
import com.katharina.sudoku.presentation.game.util.formatSeconds
import com.katharina.sudoku.ui.theme.SudokuTheme

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.mistakeCount) {
        if (uiState.mistakeCount > 0) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onDismissMessage()
        }
    }

    GameContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onCellClick = viewModel::onCellSelected,
        onNumberClick = viewModel::onNumberInput,
        onUndoClick = viewModel::onUndo,
        onRedoClick = viewModel::onRedo,
        onHintClick = viewModel::onHintRequested,
        onToggleNoteMode = viewModel::onToggleNoteMode,
        onResetClick = viewModel::onResetGame,
        onCheckClick = viewModel::onValidateBoard,
        onPauseResumeClick = viewModel::onPauseResume,
        onEraseClick = viewModel::onEraseInput,
        onPlayAgain = { viewModel.startNewGame(uiState.difficulty) },
        onBackToMenu = onBackClick,
        onBackClick = onBackClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameContent(
    uiState: GameUiState,
    snackbarHostState: SnackbarHostState,
    onCellClick: (Position) -> Unit,
    onNumberClick: (Int) -> Unit,
    onUndoClick: () -> Unit,
    onRedoClick: () -> Unit,
    onHintClick: () -> Unit,
    onToggleNoteMode: () -> Unit,
    onResetClick: () -> Unit,
    onCheckClick: () -> Unit,
    onPauseResumeClick: () -> Unit,
    onEraseClick: () -> Unit,
    onPlayAgain: () -> Unit,
    onBackToMenu: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = uiState.difficulty.name,
                                style = MaterialTheme.typography.labelMedium
                            )
                            val mistakeColor = if (uiState.mistakeCount >= 3) {
                                MaterialTheme.colorScheme.error 
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                            Text(
                                text = "Mistakes: ${uiState.mistakeCount}/3",
                                style = MaterialTheme.typography.labelSmall,
                                color = mistakeColor
                            )
                        }
                        
                        Text(
                            text = formatSeconds(uiState.timerSeconds),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        IconButton(onClick = onPauseResumeClick) {
                            Icon(
                                painter = painterResource(
                                    if (uiState.isPaused) R.drawable.ic_play_arrow_outlined 
                                    else R.drawable.ic_pause_outlined
                                ),
                                contentDescription = if (uiState.isPaused) "Resume" else "Pause"
                            )
                        }
                    }
                },
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.weight(1f)) {
                SudokuGrid(
                    uiState = uiState,
                    onCellClick = onCellClick,
                    modifier = Modifier.fillMaxSize()
                )
                
                if (uiState.isPaused) {
                    PauseOverlay(onResumeClick = onPauseResumeClick)
                }
            }

            GameControls(
                onUndoClick = onUndoClick,
                onRedoClick = onRedoClick,
                onHintClick = onHintClick,
                onToggleNoteMode = onToggleNoteMode,
                onResetClick = onResetClick,
                onCheckClick = onCheckClick,
                isNoteModeEnabled = uiState.isNoteModeEnabled
            )

            NumberPad(
                onNumberClick = onNumberClick,
                onEraseClick = onEraseClick
            )
        }
    }

    if (uiState.isComplete) {
        WinDialog(
            timerSeconds = uiState.timerSeconds,
            mistakeCount = uiState.mistakeCount,
            onPlayAgain = onPlayAgain,
            onBackToMenu = onBackToMenu
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    SudokuTheme {
        GameContent(
            uiState = GameUiState(),
            snackbarHostState = remember { SnackbarHostState() },
            onCellClick = {},
            onNumberClick = {},
            onUndoClick = {},
            onRedoClick = {},
            onHintClick = {},
            onToggleNoteMode = {},
            onResetClick = {},
            onCheckClick = {},
            onPauseResumeClick = {},
            onEraseClick = {},
            onPlayAgain = {},
            onBackToMenu = {},
            onBackClick = {}
        )
    }
}
