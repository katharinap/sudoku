package com.katharina.sudoku.presentation.game.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.katharina.sudoku.domain.model.Position
import com.katharina.sudoku.domain.model.SudokuBoard
import com.katharina.sudoku.ui.theme.SudokuTheme

@Composable
fun SudokuGrid(
    board: SudokuBoard,
    selectedPosition: Position?,
    onCellClick: (Position) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.aspectRatio(1f)) {
        val outlineColor = MaterialTheme.colorScheme.outline
        val outlineVariant = MaterialTheme.colorScheme.outlineVariant

        val selectedCell = selectedPosition?.let { board.getCell(it) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .border(2.dp, outlineColor)
        ) {
            for (row in 0 until 9) {
                Row(modifier = Modifier.weight(1f)) {
                    for (col in 0 until 9) {
                        val position = Position(row, col)
                        val cell = board.getCell(position)
                        
                        val isSelected = position == selectedPosition
                        val isPeerHighlighted = selectedPosition?.let {
                            it.row == row || it.column == col || 
                            (it.row / 3 == row / 3 && it.column / 3 == col / 3)
                        } ?: false
                        
                        val isSameNumberHighlighted = selectedCell?.value != null && 
                                cell.value == selectedCell.value && 
                                !isSelected

                        SudokuCell(
                            cell = cell,
                            isSelected = isSelected,
                            isPeerHighlighted = isPeerHighlighted,
                            isSameNumberHighlighted = isSameNumberHighlighted,
                            isError = false, // Mistake logic handled in ViewModel
                            onClick = { onCellClick(position) },
                            modifier = Modifier
                                .weight(1f)
                                .border(0.5.dp, outlineVariant)
                        )
                    }
                }
            }
        }

        // Draw thicker borders for 3x3 boxes
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 2.dp.toPx()
            val boxSize = size.width / 3
            
            // Vertical lines
            for (i in 1 until 3) {
                drawLine(
                    color = outlineColor,
                    start = Offset(i * boxSize, 0f),
                    end = Offset(i * boxSize, size.height),
                    strokeWidth = strokeWidth
                )
            }
            
            // Horizontal lines
            for (i in 1 until 3) {
                drawLine(
                    color = outlineColor,
                    start = Offset(0f, i * boxSize),
                    end = Offset(size.width, i * boxSize),
                    strokeWidth = strokeWidth
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SudokuGridPreview() {
    val board = SudokuBoard.empty()
        .withUpdatedCell(Position(0, 0)) { it.copy(value = 5, isFixed = true) }
        .withUpdatedCell(Position(4, 4)) { it.copy(value = 5) }
        .withUpdatedCell(Position(1, 1)) { it.copy(notes = setOf(1, 2, 9)) }

    SudokuTheme {
        SudokuGrid(
            board = board,
            selectedPosition = Position(4, 4),
            onCellClick = {},
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        )
    }
}
