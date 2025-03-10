package org.squidfish.tuple_n_back

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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.squidfish.tuple_n_back.models.GameSettings
import org.squidfish.tuple_n_back.models.GameStats
import org.squidfish.tuple_n_back.models.GameType
import org.squidfish.tuple_n_back.models.GameViewModel


enum class ScreenType(@StringRes val title: Int) {
    MainMenu(R.string.main_menu_screen_name),
    Game(R.string.game_screen_name),
    Summary(R.string.summary_screen_name)
}

@Composable
fun TupleNBackApp (
    gameModel: GameViewModel= GameViewModel(GameSettings(2,10,1500, games=listOf(GameType.Grid, GameType.Piano))),
    navController: NavHostController = rememberNavController()
) {
    val TAG = "TupleNBackApp"

    Scaffold(
        topBar = { TupleNBackAppBar() }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ScreenType.MainMenu.name,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(route = ScreenType.Game.name) {
                Log.v(TAG, "Composing ${ScreenType.Game}")
                GameScreen(
                    modifier = Modifier,
                    onFinnish = {
                        navController.navigate(ScreenType.Summary.name)
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
                            popUpTo(ScreenType.Game.name) {inclusive = true}
                        }

                        gameModel.resetGameState()
                        gameModel.startGame()
                    },
                    onMainMenu = {
                        navController.navigate(ScreenType.MainMenu.name) {
                            popUpTo(0)
                        }
                        gameModel.resetGameState()
                    }
                )
                Log.v(TAG, "Finished GameSummaryScreen composition")
            }
            composable(route = ScreenType.MainMenu.name) {
                Log.v(TAG, "Composing ${ScreenType.MainMenu}")
                MainMenuScreen(onStartGame = {
                    navController.navigate(ScreenType.Game.name)
                    gameModel.startGame()
                })
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TupleNBackAppBar(
    modifier: Modifier =  Modifier
) {
    TopAppBar(
        title = { Text("Tuple-N-Back") },
        //modifier = modifier
    )
}

@Preview
@Composable
fun TupleNBackAppPreview() {
    TupleNBackApp()
}
