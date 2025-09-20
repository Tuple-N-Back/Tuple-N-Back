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
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlin.reflect.typeOf
import kotlin.system.exitProcess
import org.koin.androidx.compose.koinViewModel
import org.squidfish.tuplenback.MainActivity
import org.squidfish.tuplenback.games.Game
import org.squidfish.tuplenback.presentation.navigation.LevelData
import org.squidfish.tuplenback.games.GameModule
import org.squidfish.tuplenback.games.Level
import org.squidfish.tuplenback.models.AppEvent
import org.squidfish.tuplenback.models.GameViewModel
import org.squidfish.tuplenback.presentation.navigation.AppNavTypes
import org.squidfish.tuplenback.presentation.navigation.ScreenDestination
//import org.squidfish.tuplenback.presentation.navigation.parcelableType
import org.squidfish.tuplenback.presentation.screen.gameselection.GameSelectionScreen
import org.squidfish.tuplenback.presentation.util.composable
import org.squidfish.tuplenback.utils.BadConfigurationError
import org.squidfish.tuplenback.utils.Result

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
            composable<ScreenDestination.GameScreen>(
                typeMap = AppNavTypes.typeMap
            ) { backStackEntry ->
                val tempLvl = backStackEntry.toRoute<LevelData>()
                val level = if (tempLvl.level != -1) tempLvl else null

                SideEffect {
                    Log.v(TAG, "Composed ${ScreenDestination.GameScreen::class}")
                }

                LaunchedEffect(Unit) {
                    level?.let { gameModel.onEvent(AppEvent.StartGame(it)) }
                }

                GameScreen(
                    modifier = Modifier,
                    onFinnish = {
                        gameModel.onEvent(AppEvent.FinishGame)
                        level?.let {
                            navController.navigate(ScreenDestination.SummaryScreen(level))
                        }
                    },
                    onAbort = {
                        gameModel.onEvent(AppEvent.AbortOngoingGame)
                        gameModel.onEvent(AppEvent.ResetGameState)
                        navController.navigate(ScreenDestination.MainScreen)
                    },
                    onGuess = { module: GameModule -> gameModel.onEvent(AppEvent.MakeMnemonicRepeatGuess(module)) },
                    state = gameModel.gameState.collectAsState().value,
                    game = level?.gameMode ?: gameModel.level.value?.game ?: error("Level cannot be null"),
                    totalRounds = gameModel.level.value?.settings?.totalRounds ?: error("Level cannot be null"),
                )
            }

            composable<ScreenDestination.SummaryScreen>(
                typeMap = AppNavTypes.typeMap
            ) { backStackEntry ->
                val level = backStackEntry.toRoute<LevelData>()

                SideEffect {
                    Log.v(TAG, "Composed ${ScreenDestination.SummaryScreen::class}")
                }
                GameSummaryScreen(
                    stats = gameModel.playerStats,
                    onPlayAgain = {
                        navController.navigate(ScreenDestination.GameScreen(LevelData(level.gameMode, -1))) {
                            popUpTo(ScreenDestination.GameScreen(level = level)) { inclusive = true }
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

            composable<ScreenDestination.GameSelectionScreen>(
                typeMap = AppNavTypes.typeMap
            ) {
                SideEffect {
                    Log.v(TAG, "Composed ${ScreenDestination.MainScreen::class}")
                }
                GameSelectionScreen(
                    viewModel = koinViewModel(),
                    navController = navController,
                )
            }

            composable<ScreenDestination.MainScreen>(
                typeMap = AppNavTypes.typeMap
            ) {
                MainMenuScreen(
                    onSelectGameMode = {
                        navController.navigate(ScreenDestination.GameSelectionScreen)
                    },
                    onPlayRecent = {
                        val level = gameModel.level.value.also {
                            if (it == null) Log.w(TAG, "Cannot play recent: No game played previously")
                        } ?: return@MainMenuScreen


                        // level = -1 is a marker for null!
                        navController.navigate(ScreenDestination.GameScreen(LevelData(level.game, -1)))
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
