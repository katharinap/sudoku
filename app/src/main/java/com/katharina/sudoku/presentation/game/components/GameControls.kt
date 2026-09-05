package com.katharina.sudoku.presentation.game.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.katharina.sudoku.R
import com.katharina.sudoku.ui.theme.SudokuTheme

@Composable
fun GameControls(
    onUndoClick: () -> Unit,
    onRedoClick: () -> Unit,
    onHintClick: () -> Unit,
    onToggleNoteMode: () -> Unit,
    isNoteModeEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ControlItem(
            icon = painterResource(R.drawable.ic_undo_outlined),
            label = "Undo",
            onClick = onUndoClick
        )
        ControlItem(
            icon = painterResource(R.drawable.ic_redo_outlined),
            label = "Redo",
            onClick = onRedoClick
        )
        ControlItem(
            icon = painterResource(R.drawable.ic_edit_outlined),
            label = "Notes",
            onClick = onToggleNoteMode,
            isSelected = isNoteModeEnabled
        )
        ControlItem(
            icon = painterResource(R.drawable.ic_lightbulb_outlined),
            label = "Hint",
            onClick = onHintClick
        )
    }
}

@Composable
private fun ControlItem(
    icon: Painter,
    label: String,
    onClick: () -> Unit,
    isSelected: Boolean = false
) {
    val color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.semantics {
            selected = isSelected
        }
    ) {
        IconButton(onClick = onClick) {
            Icon(
                painter = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GameControlsPreview() {
    SudokuTheme {
        GameControls(
            onUndoClick = {},
            onRedoClick = {},
            onHintClick = {},
            onToggleNoteMode = {},
            isNoteModeEnabled = true
        )
    }
}
