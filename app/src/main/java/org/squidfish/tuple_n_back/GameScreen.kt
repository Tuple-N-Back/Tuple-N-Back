package org.squidfish.tuple_n_back

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import org.squidfish.tuple_n_back.games.GridGame
import org.squidfish.tuple_n_back.models.GameModel
import org.squidfish.tuple_n_back.models.GameSettings
import org.squidfish.tuple_n_back.models.GameStats
import org.squidfish.tuple_n_back.models.GameViewModel
import org.squidfish.tuple_n_back.models.GridViewModel
import org.squidfish.tuple_n_back.models.SoundViewModel


@Composable
fun GameScreen(
    settings: GameSettings,
    onFinnish: (List<GameStats>) -> Unit,
    games: List<GameViewModel>
) {
    val TAG = "GameScreen"
    var currentTime by remember { mutableIntStateOf(0) }
    var currentRound by remember { mutableIntStateOf(0) }
    val buttonClicks = remember { mutableStateListOf<Boolean>() }

    for (i in games.indices) {
        buttonClicks.add(false)
    }

    // val gridButtonClick = remember { mutableStateOf(false) }
    // val soundButtonClick = remember { mutableStateOf(false) }
    // var gridBtnClickVal by gridButtonClick
    // var soundBtnClickVal by soundButtonClick

    val timerUpdateDelayMilli = 50

    val handler = remember {Handler(Looper.getMainLooper())}

    handler.postDelayed({
        currentTime += timerUpdateDelayMilli
    }, timerUpdateDelayMilli.toLong())

    if (currentTime >= settings.milliPerRound) {
        // gridViewModel.createNewState()
        // gridViewModel.updateStats(gridBtnClickVal)
        // gridViewModel.invokeChange()

        for (i in games.indices) {
            games[i].createNewState()
            games[i].updateStats(buttonClicks[i])
            games[i].invokeChange(LocalContext.current)

            buttonClicks[i] = false
        }

        // gridBtnClickVal = false
        // soundBtnClickVal = false

        currentTime = 0
        currentRound++
    }

    if (currentRound >= settings.totalRounds) {
        handler.removeCallbacksAndMessages(null)
        onFinnish(games.map{it.toStats()})
    }

    Log.d(TAG, "Before composable")
    Scaffold() { innerPadding ->
        Column (
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(innerPadding),
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TimerBar(settings.milliPerRound.toFloat(), currentTime.toFloat())

                Row (
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(top = 32.dp)
                ) {
                    Text(
                        text = currentRound.toString() + "/" + settings.totalRounds.toString(),
                        fontSize = TextUnit(10f, TextUnitType.Em)
                    )
                }

            }

            Log.d(TAG, "Before Grid Game")
            if (games[0] is GridViewModel) {
                GridGame(games[0] as GridViewModel)
            }
            Log.d(TAG, "After Grid Game")

            Row (
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {

                Log.d(TAG, "Before Repeat Guess Button")
                RepeatGuessButton(
                    gameViewModel = games[0],
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    isClicked = buttonClicks,
                    index = 0
                )

                Spacer(modifier = Modifier.padding(4.dp))

                RepeatGuessButton(
                    gameViewModel = games[1],
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    isClicked = buttonClicks,
                    index = 1
                )
                Log.d(TAG, "After Repeat Guess Button")
            }
        }
    }
    Log.d(TAG, "After composable")
}

@Preview
@Composable
fun GameAppPreview() {
    val gameModel = GameModel(GameSettings(4,25,1000))

    GameScreen(
        settings = gameModel.getSettings(),
        onFinnish = {},
        gameModel.getGames()
    )
}

@Composable
fun TimerBar(totalTime: Float, currentTime: Float) {

    LinearProgressIndicator(
        progress = { currentTime / totalTime},
        modifier = Modifier.fillMaxWidth(),
        color = Color.Green,
        trackColor = Color.Red
    )
}

@Composable
fun RepeatGuessButton(gameViewModel: GameViewModel, modifier: Modifier, isClicked: MutableList<Boolean>, index: Int) {
    Button(
        modifier = modifier,
        shape = RectangleShape,
        onClick = { isClicked[index] = true },
        colors = when(isClicked[index]) {
            true -> {
                buttonColors(
                    containerColor = if (gameViewModel.isRepeat) Color.Green else Color.Red
                )
            }
            false -> {
                buttonColors()
            }
        }
    ) {
        Text(gameViewModel.gameButtonText)
    }

}