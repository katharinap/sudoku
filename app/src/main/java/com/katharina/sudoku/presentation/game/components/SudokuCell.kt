package com.katharina.sudoku.presentation.game.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.katharina.sudoku.domain.model.Cell
import com.katharina.sudoku.domain.model.Position
import com.katharina.sudoku.ui.theme.ErrorNumberDark
import com.katharina.sudoku.ui.theme.ErrorNumberLight
import com.katharina.sudoku.ui.theme.FixedNumberDark
import com.katharina.sudoku.ui.theme.FixedNumberLight
import com.katharina.sudoku.ui.theme.PeerHighlightDark
import com.katharina.sudoku.ui.theme.PeerHighlightLight
import com.katharina.sudoku.ui.theme.SameNumberHighlightDark
import com.katharina.sudoku.ui.theme.SameNumberHighlightLight
import com.katharina.sudoku.ui.theme.SelectedCellHighlightDark
import com.katharina.sudoku.ui.theme.SelectedCellHighlightLight
import com.katharina.sudoku.ui.theme.SudokuTheme
import com.katharina.sudoku.ui.theme.UserNumberDark
import com.katharina.sudoku.ui.theme.UserNumberLight

@Composable
fun SudokuCell(
    cell: Cell,
    isSelected: Boolean,
    isPeerHighlighted: Boolean,
    isSameNumberHighlighted: Boolean,
    isError: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        isSelected -> if (isSystemInDarkTheme()) SelectedCellHighlightDark else SelectedCellHighlightLight
        isSameNumberHighlighted -> if (isSystemInDarkTheme()) SameNumberHighlightDark else SameNumberHighlightLight
        isPeerHighlighted -> if (isSystemInDarkTheme()) PeerHighlightDark else PeerHighlightLight
        else -> Color.Transparent
    }

    val textColor = when {
        isError -> if (isSystemInDarkTheme()) ErrorNumberDark else ErrorNumberLight
        cell.isFixed -> if (isSystemInDarkTheme()) FixedNumberDark else FixedNumberLight
        else -> if (isSystemInDarkTheme()) UserNumberDark else UserNumberLight
    }

    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(isError) {
        if (isError) {
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 300
                    (-10f) at 50 using LinearEasing
                    10f at 100 using LinearEasing
                    (-10f) at 150 using LinearEasing
                    10f at 200 using LinearEasing
                    (-5f) at 250 using LinearEasing
                    0f at 300 using LinearEasing
                }
            )
        }
    }

    val cellDescription = buildString {
        if (cell.isFixed) append("Fixed ")
        if (isError) append("Error ")
        
        val value = cell.value
        if (value != null) {
            append("Value $value")
        } else if (cell.notes.isNotEmpty()) {
            append("Notes ${cell.notes.sorted().joinToString(", ")}")
        } else {
            append("Empty cell")
        }
        append(" at row ${cell.position.row + 1} column ${cell.position.column + 1}")
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .graphicsLayer(translationX = shakeOffset.value)
            .background(backgroundColor)
            .semantics {
                contentDescription = cellDescription
                selected = isSelected
            }
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = cell.value,
            transitionSpec = {
                (scaleIn(animationSpec = tween(200)) + fadeIn()) togetherWith fadeOut()
            },
            label = "CellValueAnimation"
        ) { value ->
            if (value != null) {
                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 28.sp,
                        fontWeight = if (cell.isFixed) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = textColor
                )
            } else if (cell.notes.isNotEmpty()) {
                NotesGrid(notes = cell.notes)
            }
        }
    }
}

@Composable
private fun NotesGrid(notes: Set<Int>) {
    Column(modifier = Modifier.fillMaxSize().padding(2.dp)) {
        for (row in 0 until 3) {
            Row(modifier = Modifier.weight(1f)) {
                for (col in 0 until 3) {
                    val note = row * 3 + col + 1
                    Box(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (notes.contains(note)) {
                            Text(
                                text = note.toString(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    lineHeight = 9.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SudokuCellPreview() {
    SudokuTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SudokuCell(
                cell = Cell(Position(0, 0), value = 5, solutionValue = 5, isFixed = true),
                isSelected = false,
                isPeerHighlighted = false,
                isSameNumberHighlighted = false,
                isError = false,
                onClick = {}
            )
            SudokuCell(
                cell = Cell(Position(0, 0), value = 3, solutionValue = 5, isFixed = false),
                isSelected = true,
                isPeerHighlighted = false,
                isSameNumberHighlighted = false,
                isError = false,
                onClick = {}
            )
            SudokuCell(
                cell = Cell(Position(0, 0), notes = setOf(1, 2, 5, 9), solutionValue = 5),
                isSelected = false,
                isPeerHighlighted = true,
                isSameNumberHighlighted = false,
                isError = false,
                onClick = {}
            )
        }
    }
}
