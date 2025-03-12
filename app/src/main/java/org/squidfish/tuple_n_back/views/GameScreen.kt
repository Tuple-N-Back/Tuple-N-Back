package org.squidfish.tuple_n_back.views

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import org.squidfish.tuple_n_back.games.GridGame
import org.squidfish.tuple_n_back.games.SoundGame
import org.squidfish.tuple_n_back.models.AppEvent
import org.squidfish.tuple_n_back.models.RecallCheck
import org.squidfish.tuple_n_back.models.GameSettings
import org.squidfish.tuple_n_back.models.GameState
import org.squidfish.tuple_n_back.models.GameType
import org.squidfish.tuple_n_back.models.GameViewModel


@Composable
fun GameScreen(
    modifier: Modifier,
    onFinnish: () -> Unit,
    onAbort: () -> Unit,
    viewModel: GameViewModel,
) {
    val TAG = "GameScreen"
    val state by viewModel.gameState.collectAsState()

    if (state.gameOver) {
        LaunchedEffect(Unit) {
            onFinnish()
        }
    }

    BackHandler {
        onAbort()
    }


    Box(modifier) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TimerBar(state.roundProgress)

                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(top = 32.dp)
                ) {
                    Text(
                        text = state.currentRound.toString() + "/" +
                                viewModel.game.settings.totalRounds.toString(),
                        fontSize = TextUnit(10f, TextUnitType.Em)
                    )
                }

            }

            Games(state.mnemonicIds, viewModel.game.modules)

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                RepeatGuessButtons(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    recallGuesses = state.recallCheck,
                    games = viewModel.game.modules,
                    onGuess = { viewModel.onEvent(AppEvent.MakeMnemonicRepeatGuess(it)) }
                )
            }
        }
    }
}


@Preview
@Composable
fun GameScreenPreview() {
    val gameModel = GameViewModel()

    GameScreen(
        modifier = Modifier,
        onFinnish = {},
        onAbort = {},
        viewModel = gameModel,
    )
}

@Composable
fun TimerBar(progress: Float) {
    LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier.fillMaxWidth(),
        color = Color.Green,
        trackColor = Color.Red
    )
}

@Composable
fun RepeatGuessButton(game: GameType, recallCheck: RecallCheck?, onGuess: (game: GameType) -> Unit, modifier: Modifier) {
    Button(
        modifier = modifier,
        shape = RectangleShape,
        onClick = { if (recallCheck == RecallCheck.NONE) onGuess(game) },
        colors = when(recallCheck) {
            RecallCheck.CORRECT -> {
                buttonColors( Color.Green )
            }
            RecallCheck.INCORRECT -> {
                buttonColors( Color.Red )
            }
            RecallCheck.NONE -> {
                buttonColors()
            }
            null -> {
                Log.wtf("RepeatGuessButton", "RecallCheck is null")
                return
            }
        }
    ) {
        Text("$game")
    }

}

@Composable
fun Games(mnems: Map<GameType, Int>, games: List<GameType>) {
    games.forEach { game ->
        val mnem: Int = mnems[game] ?: return

        when(game) {
            GameType.Grid -> GridGame(mnem)
            GameType.Piano -> SoundGame(mnem)
            GameType.Colour -> TODO()
            GameType.Vibration -> TODO()
        }
    }
}

@Composable
fun RepeatGuessButtons(
    modifier: Modifier,
    recallGuesses: Map<GameType, RecallCheck>,
    games: List<GameType>,
    onGuess: (GameType) -> Unit
) {
    games.forEach { game ->
        val recallState: RecallCheck = recallGuesses[game] ?: return

        RepeatGuessButton(
            game = game,
            recallCheck = recallState,
            onGuess = { onGuess(it) },
            modifier = modifier
        )

        Spacer(Modifier.size(4.dp))
    }

}