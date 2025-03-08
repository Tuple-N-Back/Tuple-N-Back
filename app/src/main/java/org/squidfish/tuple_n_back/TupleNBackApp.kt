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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.squidfish.tuple_n_back.models.GameModel
import org.squidfish.tuple_n_back.models.GameSettings
import org.squidfish.tuple_n_back.models.GameType


enum class ScreenType(@StringRes val title: Int) {
    MainMenu(R.string.main_menu_screen_name),
    Game(R.string.game_screen_name),
    Summary(R.string.summary_screen_name)
}

@Composable
fun TupleNBackApp (
    gameModel: GameModel = GameModel(GameSettings(2,10,500)),
    navController: NavHostController = rememberNavController()
) {
    val TAG = "TupleNBackApp"
    gameModel.setGames(listOf<GameType>(GameType.Grid, GameType.Sound))

    Scaffold(
        topBar = { TupleNBackAppBar() }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ScreenType.Game.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            Log.d(TAG, "in navhost")
            composable(route = ScreenType.Game.name) {
                Log.d(TAG, "in composable")
                GameScreen(
                    settings = gameModel.getSettings(),
                    onFinnish = {
                        gameModel.setStats(it)
                        navController.navigate(ScreenType.Summary.name)
                    },
                    gameModel.getGames()
                )
                Log.d(TAG, "out of composable")
            }
            composable(route = ScreenType.Summary.name) {
                GameSummaryScreen()
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
        modifier = modifier
    )
}
