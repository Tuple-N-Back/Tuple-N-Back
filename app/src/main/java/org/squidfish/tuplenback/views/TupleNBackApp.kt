package org.squidfish.tuplenback.views

import android.util.Log
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
import androidx.navigation.compose.rememberNavController
import kotlin.system.exitProcess
import org.koin.androidx.compose.koinViewModel
import org.squidfish.tuplenback.MainActivity
import org.squidfish.tuplenback.games.GameModule
import org.squidfish.tuplenback.models.AppEvent
import org.squidfish.tuplenback.models.GameViewModel
import org.squidfish.tuplenback.presentation.navigation.ScreenDestination
import org.squidfish.tuplenback.presentation.screen.gameselection.GameSelectionScreen
import org.squidfish.tuplenback.presentation.util.composable

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
            startDestination = ScreenDestination.MainScreen,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable<ScreenDestination.GameScreen> { backStackEntry ->
                SideEffect {
                    Log.v(TAG, "Composed ${ScreenDestination.GameScreen::class}")
                }
                LaunchedEffect(Unit) {
                    backStackEntry.game?.let { gameModel.onEvent(AppEvent.StartGame(it)) }
                }
                GameScreen(
                    modifier = Modifier,
                    onFinnish = {
                        (backStackEntry.game ?: gameModel.game)?.let {
                            navController.navigate(ScreenDestination.SummaryScreen(game = it))
                        }
                    },
                    onAbort = {
                        gameModel.onEvent(AppEvent.AbortOngoingGame)
                        gameModel.onEvent(AppEvent.ResetGameState)
                        navController.navigate(ScreenDestination.MainScreen)
                    },
                    onGuess = { module: GameModule -> gameModel.onEvent(AppEvent.MakeMnemonicRepeatGuess(module)) },
                    state = gameModel.gameState.collectAsState().value,
                    game = backStackEntry.game ?: gameModel.game ?: throw IllegalStateException("Game cannot be null"),
                )
            }
            composable<ScreenDestination.SummaryScreen> { backStackEntry ->
                SideEffect {
                    Log.v(TAG, "Composed ${ScreenDestination.SummaryScreen::class}")
                }
                GameSummaryScreen(
                    stats = gameModel.playerStats,
                    onPlayAgain = {
                        navController.navigate(ScreenDestination.GameScreen(game = null)) {
                            popUpTo(ScreenDestination.GameScreen(game = backStackEntry.game)) { inclusive = true }
                        }

                        gameModel.onEvent(AppEvent.ResetGameState)
                        gameModel.onEvent(AppEvent.PlayAgain)
                    },
                    onMainMenu = {
                        navController.navigate(ScreenDestination.MainScreen) {
                            popUpTo(ScreenDestination.MainScreen) { inclusive = true }
                        }

                        gameModel.onEvent(AppEvent.ResetGameState)
                    },
                )
            }

            composable<ScreenDestination.GameSelectionScreen> {
                SideEffect {
                    Log.v(TAG, "Composed ${ScreenDestination.MainScreen::class}")
                }
                GameSelectionScreen(
                    viewModel = koinViewModel(),
                    navController = navController,
                )
            }

            composable<ScreenDestination.MainScreen> {
                MainMenuScreen(
                    onSelectGameMode = {
                        navController.navigate(ScreenDestination.GameSelectionScreen)
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
