package org.squidfish.tuple_n_back

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
import org.squidfish.tuple_n_back.models.RecallCheck
import org.squidfish.tuple_n_back.models.GameSettings
import org.squidfish.tuple_n_back.models.GameType
import org.squidfish.tuple_n_back.models.GameViewModel
import org.squidfish.tuple_n_back.models.GridPosition


@Composable
fun GameScreen(
    settings: GameSettings,
    onFinnish: () -> Unit,
    viewModel: GameViewModel = GameViewModel(GameSettings(2, 10, 200)),
    onMnemGuess: (game: GameType) -> Unit
    //games: List<GameEngine>
) {
    val TAG = "GameScreen"
    val state by viewModel.gameState.collectAsState()

    // if game has ended - this is when game stats are updated
    // TODO: more robust check
    if (state.gameOver) {
        GameSummaryScreen(state.gameStats)
        return
    }

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
                TimerBar(state.roundProgress)

                Row (
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(top = 32.dp)
                ) {
                    Text(
                        text = state.currentRound.toString() + "/" + settings.totalRounds.toString(),
                        fontSize = TextUnit(10f, TextUnitType.Em)
                    )
                }

            }

            GridGame(state.mnemonicIds[GameType.Grid] ?:
                        throw IllegalStateException("GameState doesn't include Grid game"))
            SoundGame(state.mnemonicIds[GameType.Sound] ?:
                        throw IllegalStateException("GameState doesn't include Sound game"))

            Row (
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                RepeatGuessButton(
                    game = GameType.Grid,
                    recallCheck = state.recallCheck[GameType.Grid],
                    onGuess = onMnemGuess,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                )

                Spacer(modifier = Modifier.padding(4.dp))

                RepeatGuessButton(
                    game = GameType.Sound,
                    recallCheck = state.recallCheck[GameType.Sound],
                    onGuess = onMnemGuess,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                )
            }
        }
    }
}

@Preview
@Composable
fun GameAppPreview() {
    val gameModel = GameViewModel(GameSettings(4,25,1000))

    GameScreen(
        settings = gameModel.gameSettings,
        onFinnish = {},
        viewModel = gameModel,
        onMnemGuess = gameModel::handleGuess
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
        onClick = { onGuess(game) },
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