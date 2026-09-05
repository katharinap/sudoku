package com.katharina.sudoku.presentation.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.katharina.sudoku.R
import com.katharina.sudoku.domain.model.Difficulty
import com.katharina.sudoku.domain.model.GameStats
import com.katharina.sudoku.presentation.game.util.formatSeconds
import com.katharina.sudoku.ui.theme.SudokuTheme

@Composable
fun StatsScreen(
    viewModel: StatsViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    StatsContent(
        uiState = uiState,
        onBackClick = onBackClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsContent(
    uiState: StatsUiState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics") },
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
            val statsList = Difficulty.entries.map { difficulty ->
                uiState.statsByDifficulty[difficulty] ?: GameStats(difficulty, 0, 0, 0L)
            }

            if (statsList.all { it.gamesPlayed == 0 }) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No games played yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    statsList.forEach { stats ->
                        StatCard(stats = stats)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(stats: GameStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stats.difficulty.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(label = "Played", value = stats.gamesPlayed.toString())
                val winRate = if (stats.gamesPlayed > 0) {
                    (stats.gamesWon.toDouble() / stats.gamesPlayed * 100).toInt()
                } else 0
                StatItem(label = "Won", value = "${stats.gamesWon} ($winRate%)")
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Best Time",
                    value = if (stats.bestTimeSeconds > 0) formatSeconds(stats.bestTimeSeconds) else "--:--"
                )
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StatsScreenPreview() {
    val mockStats = mapOf(
        Difficulty.EASY to GameStats(Difficulty.EASY, 10, 8, 45L),
        Difficulty.MEDIUM to GameStats(Difficulty.MEDIUM, 5, 2, 180L)
    )
    SudokuTheme {
        StatsContent(
            uiState = StatsUiState(statsByDifficulty = mockStats, isLoading = false),
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StatsScreenEmptyPreview() {
    SudokuTheme {
        StatsContent(
            uiState = StatsUiState(statsByDifficulty = emptyMap(), isLoading = false),
            onBackClick = {}
        )
    }
}
