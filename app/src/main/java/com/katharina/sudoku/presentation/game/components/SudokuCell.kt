package com.katharina.sudoku.presentation.game.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.katharina.sudoku.domain.model.Cell
import com.katharina.sudoku.domain.model.Position
import com.katharina.sudoku.ui.theme.*

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

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (cell.value != null) {
            Text(
                text = cell.value.toString(),
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
                cell = Cell(Position(0, 0), value = 5, isFixed = true),
                isSelected = false,
                isPeerHighlighted = false,
                isSameNumberHighlighted = false,
                isError = false,
                onClick = {}
            )
            SudokuCell(
                cell = Cell(Position(0, 0), value = 3, isFixed = false),
                isSelected = true,
                isPeerHighlighted = false,
                isSameNumberHighlighted = false,
                isError = false,
                onClick = {}
            )
            SudokuCell(
                cell = Cell(Position(0, 0), notes = setOf(1, 2, 5, 9)),
                isSelected = false,
                isPeerHighlighted = true,
                isSameNumberHighlighted = false,
                isError = false,
                onClick = {}
            )
        }
    }
}
