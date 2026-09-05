package com.katharina.sudoku.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.katharina.sudoku.presentation.game.GameScreen
import com.katharina.sudoku.presentation.game.GameViewModel
import com.katharina.sudoku.presentation.menu.MenuScreen
import com.katharina.sudoku.presentation.menu.MenuViewModel
import com.katharina.sudoku.presentation.settings.SettingsScreen
import com.katharina.sudoku.presentation.settings.SettingsViewModel
import com.katharina.sudoku.presentation.stats.StatsScreen
import com.katharina.sudoku.presentation.stats.StatsViewModel
import com.katharina.sudoku.domain.model.Difficulty
import kotlinx.serialization.Serializable

@Serializable
object MenuRoute

@Serializable
data class GameRoute(val difficulty: Difficulty? = null)

@Serializable
object SettingsRoute

@Serializable
object StatsRoute

@Composable
fun SudokuNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = MenuRoute,
        modifier = modifier
    ) {
        composable<MenuRoute> {
            val viewModel: MenuViewModel = hiltViewModel()
            MenuScreen(
                viewModel = viewModel,
                onContinueClick = {
                    navController.navigate(GameRoute(difficulty = null))
                },
                onNewGameClick = { difficulty ->
                    navController.navigate(GameRoute(difficulty = difficulty))
                },
                onStatsClick = {
                    navController.navigate(StatsRoute)
                },
                onSettingsClick = {
                    navController.navigate(SettingsRoute)
                }
            )
        }

        composable<GameRoute> {
            val viewModel: GameViewModel = hiltViewModel()
            
            GameScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable<SettingsRoute> {
            val viewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable<StatsRoute> {
            val viewModel: StatsViewModel = hiltViewModel()
            StatsScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
