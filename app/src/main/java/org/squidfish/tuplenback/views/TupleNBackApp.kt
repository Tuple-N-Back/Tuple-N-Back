package org.squidfish.tuplenback.views

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlin.system.exitProcess
import org.koin.androidx.compose.koinViewModel
import org.squidfish.tuplenback.MainActivity
import org.squidfish.tuplenback.R
import org.squidfish.tuplenback.games.GameModule
import org.squidfish.tuplenback.models.AppEvent
import org.squidfish.tuplenback.models.GameViewModel
import org.squidfish.tuplenback.presentation.navigation.ScreenDestination
import org.squidfish.tuplenback.presentation.screen.gameselection.GameSelectionScreen

enum class ScreenType(@param:StringRes val title: Int) {
    GameSelection(R.string.game_selection_screen_name),
    MainMenu(R.string.main_menu_screen_name),
}

private const val TAG = "TupleNBackApp"

@Composable
fun TupleNBackApp(
    gameModel: GameViewModel = koinViewModel(),
    navController: NavHostController = rememberNavController(),
) {
    Scaffold(
        topBar = { TupleNBackAppBar() },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ScreenType.MainMenu.name,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable<ScreenDestination.GameScreen> { backStackEntry ->
                val navEntry = backStackEntry.toRoute<ScreenDestination.GameScreen>()
                SideEffect {
                    Log.v(TAG, "Composed ${ScreenDestination.GameScreen::class}")
                }
                LaunchedEffect(Unit) {
                    navEntry.game?.let { gameModel.onEvent(AppEvent.StartGame(it)) }
                }
                GameScreen(
                    modifier = Modifier,
                    onFinnish = {
                        (navEntry.game ?: gameModel.game)?.let {
                            navController.navigate(ScreenDestination.SummaryScreen(game = it))
                        }
                    },
                    onAbort = {
                        gameModel.onEvent(AppEvent.AbortOngoingGame)
                        gameModel.onEvent(AppEvent.ResetGameState)
                        navController.navigate(ScreenType.MainMenu.name)
                    },
                    onGuess = { module: GameModule -> gameModel.onEvent(AppEvent.MakeMnemonicRepeatGuess(module)) },
                    state = gameModel.gameState.collectAsState().value,
                    game = gameModel.game ?: throw IllegalStateException("Game cannot be null"),
                )
            }
            composable<ScreenDestination.SummaryScreen> { backStackEntry ->
                val navEntry = backStackEntry.toRoute<ScreenDestination.SummaryScreen>()
                SideEffect {
                    Log.v(TAG, "Composed ${ScreenDestination.SummaryScreen::class}")
                }
                GameSummaryScreen(
                    stats = gameModel.playerStats,
                    onPlayAgain = {
                        navController.navigate(ScreenDestination.GameScreen(game = null)) {
                            popUpTo(ScreenDestination.GameScreen(game = navEntry.game)) { inclusive = true }
                        }

                        gameModel.onEvent(AppEvent.ResetGameState)
                        gameModel.onEvent(AppEvent.PlayAgain)
                    },
                    onMainMenu = {
                        navController.navigate(ScreenType.MainMenu.name) {
                            popUpTo(ScreenType.MainMenu.name)
                        }

                        gameModel.onEvent(AppEvent.ResetGameState)
                    },
                )
            }

            composable(route = ScreenType.GameSelection.name) {
                SideEffect {
                    Log.v(TAG, "Composed ${ScreenType.GameSelection}")
                }
                GameSelectionScreen(
                    viewModel = koinViewModel(),
                    navController = navController,
                )
            }

            composable(route = ScreenType.MainMenu.name) {
                MainMenuScreen(
                    onSelectGameMode = {
                        navController.navigate(ScreenType.GameSelection.name)
                    },
                    onPlayRecent = {
                        if (gameModel.game == null) {
                            Log.w(TAG, "Cannot play recent: No game played previously")
                            return@MainMenuScreen
                        }

                        navController.navigate(ScreenDestination.GameScreen(game = null))
                        gameModel.onEvent(AppEvent.PlayAgain)
                    },
                    onSettings = {},
                    onInfo = {},
                    onExit = {
                        MainActivity().finish()
                        exitProcess(0)
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TupleNBackAppBar(modifier: Modifier = Modifier) {
    TopAppBar(
        title = { Text("Tuple-N-Back") },
        modifier = modifier,
    )
}

@Preview
@Composable
fun TupleNBackAppPreview() {
    TupleNBackApp()
}
