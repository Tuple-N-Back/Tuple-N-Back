package org.squidfish.tuplenback.views

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.squidfish.tuplenback.games.GameModule
import org.squidfish.tuplenback.games.modules.GridGame
import org.squidfish.tuplenback.games.modules.SoundGame
import org.squidfish.tuplenback.models.AppEvent
import org.squidfish.tuplenback.models.GameViewModel
import org.squidfish.tuplenback.models.RecallCheck

@Composable
fun GameScreen(
    modifier: Modifier,
    onFinnish: () -> Unit,
    onAbort: () -> Unit,
    viewModel: GameViewModel,
) {
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
                .fillMaxWidth(),
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TimerBar(state.roundProgress)

                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(top = 32.dp),
                ) {
                    Text(
                        text = "${state.currentRound}/${viewModel.game.settings.totalRounds}",
                        fontSize = 10.sp,
                    )
                }
            }

            Games(state.mnemonicIds, viewModel.game.modules)

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
            ) {
                RepeatGuessButtons(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    recallGuesses = state.recallCheck,
                    games = viewModel.game.modules,
                    onGuess = { viewModel.onEvent(AppEvent.MakeMnemonicRepeatGuess(it)) },
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
        trackColor = Color.Red,
    )
}

@Composable
fun RepeatGuessButton(
    game: GameModule,
    recallCheck: RecallCheck?,
    onGuess: (game: GameModule) -> Unit,
    modifier: Modifier,
) {
    Button(
        modifier = modifier,
        shape = RectangleShape,
        onClick = { if (recallCheck == RecallCheck.NONE) onGuess(game) },
        colors = when (recallCheck) {
            RecallCheck.CORRECT -> buttonColors(Color.Green)
            RecallCheck.INCORRECT -> buttonColors(Color.Red)
            RecallCheck.NONE -> buttonColors()
            null -> {
                Log.wtf("RepeatGuessButton", "RecallCheck is null")
                return
            }
        },
    ) {
        Text("$game")
    }
}

@Composable
fun Games(mnems: Map<GameModule, Int>, games: List<GameModule>) {
    games.forEach { game ->
        val mnem: Int = mnems[game] ?: return

        when (game) {
            GameModule.Grid -> GridGame(mnem)
            GameModule.Piano -> SoundGame(mnem)
            GameModule.Colour -> TODO()
            GameModule.Vibration -> TODO()
        }
    }
}

@Composable
fun RepeatGuessButtons(
    modifier: Modifier,
    recallGuesses: Map<GameModule, RecallCheck>,
    games: List<GameModule>,
    onGuess: (GameModule) -> Unit,
) {
    games.forEach { game ->
        val recallState: RecallCheck = recallGuesses[game] ?: return

        RepeatGuessButton(
            game = game,
            recallCheck = recallState,
            onGuess = { onGuess(it) },
            modifier = modifier,
        )

        Spacer(Modifier.size(4.dp))
    }
}
