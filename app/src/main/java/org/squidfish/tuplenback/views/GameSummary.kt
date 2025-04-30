package org.squidfish.tuplenback.views

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.squidfish.tuplenback.data.FakeRepository
import org.squidfish.tuplenback.games.Game
import org.squidfish.tuplenback.games.GameModule
import org.squidfish.tuplenback.models.AppEvent
import org.squidfish.tuplenback.models.GameStatsModel
import org.squidfish.tuplenback.models.GameViewModel

private const val TAG = "GameScreen"

@Composable
fun GameSummaryScreen(
    viewModel: GameViewModel,
    onPlayAgain: () -> Unit,
    onMainMenu: () -> Unit,
) {
    val state by viewModel.gameState.collectAsState()
    val sortedStats = remember(state.gameStatsModel) { state.gameStatsModel.toSortedMap().entries.toList() }

    Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxHeight()) {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(sortedStats) { (game, stat) ->
                StatCard(Modifier.fillMaxWidth().padding(bottom = 12.dp), game, stat)
            }
        }

        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RectangleShape,
                onClick = {
                    Log.d(TAG, "Pressed PlayAgain button")
                    onPlayAgain()
                },
            ) {
                Text("Play again")
            }

            Spacer(modifier = Modifier.padding(4.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RectangleShape,
                onClick = {
                    Log.d(TAG, "Pressed MainMenu button")
                    onMainMenu()
                },
            ) {
                Text("Main Menu")
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier,
    game: GameModule,
    stats: GameStatsModel,
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        // Icon and name
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp, top = 8.dp, start = 8.dp),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(game.toGameIconRes()),
                contentDescription = "$game",
                modifier = Modifier.size(40.dp).padding(top = 2.dp),
                tint = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "$game",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        }

        // Stats
        Column {
            StatRow("Correct", stats.correctRecalls.toString())
            StatRow("Incorrect", stats.incorrectRecalls.toString())
            StatRow("Missed", stats.missedRecalls.toString())
        }
    }
}

@Composable
private fun StatRow(name: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GameSummaryScreenPreview() {

    val vm = GameViewModel(FakeRepository())
    vm.game = Game.GridPiano
    vm.onEvent(AppEvent.ResetGameState)
    GameSummaryScreen(vm, {}, {})
}
