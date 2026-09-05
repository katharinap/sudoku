package com.katharina.sudoku.presentation.game.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.katharina.sudoku.presentation.game.util.formatSeconds
import com.katharina.sudoku.ui.theme.SudokuTheme

@Composable
fun WinDialog(
    timerSeconds: Long,
    mistakeCount: Int,
    onPlayAgain: () -> Unit,
    onBackToMenu: () -> Unit
) {
    val scale = remember { Animatable(0.8f) }
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    AlertDialog(
        onDismissRequest = { /* Don't dismiss by clicking outside */ },
        modifier = Modifier.graphicsLayer(scaleX = scale.value, scaleY = scale.value),
        title = {
            Text(text = "Congratulations!")
        },
        text = {
            Column {
                Text(text = "You solved the puzzle!")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Time: ${formatSeconds(timerSeconds)}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Mistakes: $mistakeCount",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onPlayAgain) {
                Text("Play Again")
            }
        },
        dismissButton = {
            TextButton(onClick = onBackToMenu) {
                Text("Back to Menu")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun WinDialogPreview() {
    SudokuTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            WinDialog(
                timerSeconds = 125,
                mistakeCount = 1,
                onPlayAgain = {},
                onBackToMenu = {}
            )
        }
    }
}
