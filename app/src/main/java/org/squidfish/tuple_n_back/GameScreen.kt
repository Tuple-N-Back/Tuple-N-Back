package org.squidfish.tuple_n_back

import android.media.MediaPlayer
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
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
    gridViewModel: GridViewModel,
    soundViewModel: SoundViewModel,
    time: Float
) {
    var currentTime by remember { mutableFloatStateOf(0f) }
    var gridButtonClick by remember { mutableStateOf(false) }
    var soundButtonClick by remember { mutableStateOf(false) }

    Handler(Looper.getMainLooper()).postDelayed({
        currentTime += 0.01f
    }, 10)

    if (currentTime >= time) {
        gridViewModel.createNewState()

        soundViewModel.createNewState()
        soundViewModel.invokeChange(LocalContext.current)
//        val mp = MediaPlayer.create(LocalContext.current, R.raw.key01)
//        mp.start()

        gridButtonClick = false
        soundButtonClick = false
        currentTime = 0f
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
            TimerBar(time, currentTime)

            GridGame(gridViewModel.getRecent())

            Row (
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RectangleShape,
                    onClick = {
                        gridButtonClick = true
                    },
                    colors = when(gridButtonClick) {
                        true -> {
                            buttonColors(
                                containerColor = if (gridViewModel.isRepeat) Color.Green else Color.Red
                            )
                        }
                        false -> {
                            buttonColors()
                        }
                    }
                ) {
                    Text("Grid")
                }

                Spacer(modifier = Modifier.padding(4.dp))

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RectangleShape,
                    onClick = {
                        soundButtonClick = true
                    },
                    colors = when(soundButtonClick) {
                        true -> {
                            buttonColors(
                                containerColor = if (soundViewModel.isRepeat) Color.Green else Color.Red
                            )
                        }
                        false -> {
                            buttonColors()
                        }
                    }
                ) {
                    Text("Sound")
                }
            }
        }
    }
}

@Preview
@Composable
fun GameAppPreview() {
    GameApp(GridViewModel(4), SoundViewModel(4, 9), 5f)
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

// TODO: button function or button colour when clicked function