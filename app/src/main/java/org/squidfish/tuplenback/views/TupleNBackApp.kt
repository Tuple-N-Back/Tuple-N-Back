package org.squidfish.tuplenback.views

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlin.system.exitProcess
import org.squidfish.tuplenback.MainActivity
import org.squidfish.tuplenback.R
import org.squidfish.tuplenback.games.Game
import org.squidfish.tuplenback.models.AppEvent
import org.squidfish.tuplenback.models.GameViewModel

enum class ScreenType(@StringRes val title: Int) {
    GameSelection(R.string.game_selection_screen_name),
    Game(R.string.game_screen_name),
    Summary(R.string.summary_screen_name),
    MainMenu(R.string.main_menu_screen_name),
}

private const val TAG = "TupleNBackApp"

@Composable
fun TupleNBackApp(
    gameModel: GameViewModel = viewModel(factory = GameViewModel.Factory),
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
            composable(route = ScreenType.Game.name) {
                Log.v(TAG, "Composing ${ScreenType.Game}")
                GameScreen(
                    modifier = Modifier,
                    onFinnish = {
                        navController.navigate(ScreenType.Summary.name)
                    },
                    onAbort = {
                        gameModel.onEvent(AppEvent.AbortOngoingGame)
                        gameModel.onEvent(AppEvent.ResetGameState)
                        navController.navigate(ScreenType.MainMenu.name)
                    },
                    viewModel = gameModel,
                )
            }
            composable(route = ScreenType.Summary.name) {
                Log.v(TAG, "Composing ${ScreenType.Summary}")
                GameSummaryScreen(
                    viewModel = gameModel,
                    onPlayAgain = {
                        navController.navigate(ScreenType.Game.name) {
                            popUpTo(ScreenType.Game.name) { inclusive = true }
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
                Log.v(TAG, "Finished GameSummaryScreen composition")
            }

            composable(route = ScreenType.GameSelection.name) {
                Log.v(TAG, "Composing ${ScreenType.GameSelection}")
                GameSelectionScreen(onStartGame = { game: Game ->
                    navController.navigate(ScreenType.Game.name)
                    gameModel.onEvent(AppEvent.StartGame(game))
                })
            }

            composable(route = ScreenType.MainMenu.name) {
                MainMenuScreen(
                    onSelectGameMode = {
                        navController.navigate(ScreenType.GameSelection.name)
                    },
                    onPlayRecent = {
                        if (gameModel.game == Game.None) {
                            Log.w(TAG, "Cannot play recent: No game played previously")
                            return@MainMenuScreen
                        }

                        navController.navigate(ScreenType.Game.name)
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
