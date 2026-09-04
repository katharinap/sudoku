package com.katharina.sudoku.presentation.game.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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
        val primary = MaterialTheme.colorScheme.primary

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
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .background(if (isSelected) primary.copy(alpha = 0.2f) else Color.Transparent)
                                .border(
                                    width = 0.5.dp,
                                    color = outlineVariant
                                )
                                .clickable { onCellClick(position) }
                                .padding(1.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Placeholder for SudokuCell
                            Text(
                                text = cell.value?.toString() ?: "",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
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
    SudokuTheme {
        SudokuGrid(
            board = SudokuBoard.empty(),
            selectedPosition = Position(4, 4),
            onCellClick = {},
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        )
    }
}
