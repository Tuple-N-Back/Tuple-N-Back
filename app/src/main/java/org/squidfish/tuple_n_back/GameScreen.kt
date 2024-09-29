package org.squidfish.tuple_n_back

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.squidfish.tuple_n_back.games.GridGame
import org.squidfish.tuple_n_back.models.GameModel
import org.squidfish.tuple_n_back.models.GameViewModel
import org.squidfish.tuple_n_back.models.GridViewModel
import org.squidfish.tuple_n_back.models.SoundViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameAppBar(
    modifier: Modifier =  Modifier
) {
    TopAppBar(
        title = {Text("Tuple-N-Back")},
        modifier = modifier
    )
}

@Composable
fun GameApp(
    gameModel: GameModel,
    gridViewModel: GridViewModel,
    soundViewModel: SoundViewModel,
) {
    var currentTime by remember { mutableIntStateOf(0) }
    val gridButtonClick = remember { mutableStateOf(false) }
    val soundButtonClick = remember { mutableStateOf(false) }

    val timerUpdateDelayMilli = 10

    Handler(Looper.getMainLooper()).postDelayed({
        currentTime += timerUpdateDelayMilli
    }, timerUpdateDelayMilli.toLong())

    if (currentTime >= gameModel.milliPerRound) {
        gridViewModel.createNewState()
        gridViewModel.invokeChange(LocalContext.current)

        soundViewModel.createNewState()
        soundViewModel.invokeChange(LocalContext.current)

        var gridBtnClickVal by gridButtonClick
        var soundBtnClickVal by soundButtonClick

        gridBtnClickVal = false
        soundBtnClickVal = false

        currentTime = 0
        gameModel.currentRound++
    }

    Scaffold(
        topBar = {
            GameAppBar()
        }
    ) { innerPadding ->
        Column (
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(innerPadding),
        ) {
            TimerBar(gameModel.milliPerRound.toFloat(), currentTime.toFloat())

            GridGame(gridViewModel)

            Row (
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {

                RepeatGuessButton(
                    gameViewModel = gridViewModel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    isClicked = gridButtonClick
                )

                Spacer(modifier = Modifier.padding(4.dp))

                RepeatGuessButton(
                    gameViewModel = soundViewModel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    isClicked = soundButtonClick
                )
            }
        }
    }
}

@Preview
@Composable
fun GameAppPreview() {
    val gameModel = GameModel(4, 25, 1500)
    GameApp(gameModel, GridViewModel(gameModel), SoundViewModel(gameModel))
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
fun RepeatGuessButton(gameViewModel: GameViewModel, modifier: Modifier, isClicked: MutableState<Boolean>) {
    var buttonClick by isClicked
    Button(
        modifier = modifier,
        shape = RectangleShape,
        onClick = { buttonClick = true },
        colors = when(buttonClick) {
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
// TODO: button function or button colour when clicked function