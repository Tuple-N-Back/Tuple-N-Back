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
import org.squidfish.tuplenback.models.GameLoadingState
import org.squidfish.tuplenback.models.GameViewModel
import org.squidfish.tuplenback.presentation.navigation.LevelData
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
                val level = backStackEntry.game?.let {
                    LevelData(it, backStackEntry.level)
                }

                SideEffect {
                    Log.v(TAG, "Composed ${ScreenDestination.GameScreen::class}")
                }

                LaunchedEffect(Unit) {
                    // TODO: If necessary, split AppEvent.StartGame into initialization and start-round events. This
                    //  way, initialization should happen before the composable is called and the round should start
                    //  after (by passing onStart to the GameScreen?)
                    level?.let { gameModel.onEvent(AppEvent.StartGame(it)) }
                }

                when (gameModel.gameLoadingState.value) {
                    is GameLoadingState.Loading -> {
                        // TODO: Loading screen
                    }
                    is GameLoadingState.Success -> {
                        val levelData = gameModel.gameLoadingState.value as GameLoadingState.Success

                        GameScreen(
                            modifier = Modifier,
                            onFinnish = {
                                gameModel.onEvent(AppEvent.FinishGame)
                                level?.let {
                                    navController.navigate(ScreenDestination.SummaryScreen(it.gameMode, it.level))
                                    return@GameScreen
                                }
                                gameModel.level.value?.let {
                                    navController.navigate(ScreenDestination.SummaryScreen(it.game, it.level))
                                }
                            },
                            onAbort = {
                                gameModel.onEvent(AppEvent.AbortOngoingGame)
                                gameModel.onEvent(AppEvent.ResetGameState)
                                navController.navigate(ScreenDestination.MainScreen)
                            },
                            onGuess = { module: GameModule ->
                                gameModel.onEvent(AppEvent.MakeMnemonicRepeatGuess(module))
                            },
                            state = gameModel.gameState.collectAsState().value,
                            game = levelData.level.game,
                            totalRounds = levelData.level.settings.totalRounds,
                        )
                    }
                    is GameLoadingState.Failiure -> {
                        val error = gameModel.gameLoadingState.value as GameLoadingState.Failiure
                        Log.e(TAG, "Game loading error: ${error.error}")
                        // TODO: Error display
                    }
                }
            }

            composable<ScreenDestination.SummaryScreen> { backStackEntry ->
                val level = LevelData(backStackEntry.game, backStackEntry.level)

                SideEffect {
                    Log.v(TAG, "Composed ${ScreenDestination.SummaryScreen::class}")
                }
                GameSummaryScreen(
                    stats = gameModel.playerStats,
                    onPlayAgain = {
                        navController.navigate(ScreenDestination.GameScreen(null, 0)) {
                            popUpTo(ScreenDestination.GameScreen(level.gameMode, level.level)) { inclusive = true }
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
                        if (gameModel.level.value == null) {
                            Log.w(TAG, "Cannot play recent: No game played previously")
                            return@MainMenuScreen
                        }

                        navController.navigate(ScreenDestination.GameScreen(null, 0))
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
