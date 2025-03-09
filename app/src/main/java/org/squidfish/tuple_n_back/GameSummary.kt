package org.squidfish.tuple_n_back

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.squidfish.tuple_n_back.models.GameStats
import org.squidfish.tuple_n_back.models.GameType


@Composable
fun GameSummaryScreen(
    stats: Map<GameType, GameStats>
) {

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        stats.forEach { (game, stats) ->
            Text(
                "Game: $game" +
                "\nCorrect guesses: ${stats.correctRecalls}" +
                "\nIncorrect guesses: ${stats.incorrectRecalls}" +
                "\nMissed guesses: ${stats.missedRecalls}"
            )

            Spacer(Modifier.padding(4.dp))
        }
    }
}