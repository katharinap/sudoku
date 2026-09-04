package com.katharina.sudoku.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ThemePreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Sudoku Theme Preview", style = MaterialTheme.typography.titleLarge)
        
        Text(text = "Colors", style = MaterialTheme.typography.titleMedium)
        ColorRow("Primary", MaterialTheme.colorScheme.primary)
        ColorRow("Secondary", MaterialTheme.colorScheme.secondary)
        ColorRow("Error", MaterialTheme.colorScheme.error)
        
        Text(text = "Sudoku Grid Colors (Light)", style = MaterialTheme.typography.titleMedium)
        ColorRow("Fixed", FixedNumberLight)
        ColorRow("User", UserNumberLight)
        ColorRow("Highlight", SelectedCellHighlightLight)
        
        Text(text = "Shapes", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small)
            )
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(MaterialTheme.colorScheme.secondary, MaterialTheme.shapes.medium)
            )
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(MaterialTheme.colorScheme.tertiary, MaterialTheme.shapes.large)
            )
        }

        Text(text = "Typography", style = MaterialTheme.typography.titleMedium)
        Text(text = "Display Large (Numbers?)", style = MaterialTheme.typography.displayLarge)
        Text(text = "Body Large", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Label Small", style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun ColorRow(label: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(color, RoundedCornerShape(4.dp))
        )
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun ThemePreviewLight() {
    SudokuTheme(darkTheme = false) {
        Surface {
            ThemePreview()
        }
    }
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun ThemePreviewDark() {
    SudokuTheme(darkTheme = true) {
        Surface {
            ThemePreview()
        }
    }
}
