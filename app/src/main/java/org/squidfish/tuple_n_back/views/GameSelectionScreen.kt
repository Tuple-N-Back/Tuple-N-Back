package org.squidfish.tuple_n_back.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.squidfish.tuple_n_back.models.Game

@Composable
fun GameSelectionScreen(onStartGame: (Game) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val games = remember() {
            Game.entries.filter { it != Game.None}.toTypedArray()
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Top
        ) {
            items(games) { game ->
                // TODO: fetch highestLvl and totalGames
                GameCard(game, 2, 123) { onStartGame(game) }
            }
        }
    }
}

@Composable
fun GameCard(games: Game, highestLvl: Int, totalGames: Long, onClick: () -> Unit) {
    // on-click open pop-up
    Card(
        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 8.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically)
        {
            Row(modifier = Modifier, horizontalArrangement = Arrangement.Start) {
                // Game text
                games.modules.forEach { game ->
                    Text("$game ", style = MaterialTheme.typography.displaySmall)
                }
            }

            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                games.modules.forEach { game ->
                    Icon(
                        imageVector = ImageVector.vectorResource(game.toGameIconRes()),
                        contentDescription = "Grid and Piano game",
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(Modifier.size(4.dp))
                }
            }
        }

        Spacer(Modifier.size(12.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp, end = 20.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column() {
                Text("Level: $highestLvl", style = MaterialTheme.typography.headlineMedium)
                Text("Games: $totalGames", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Preview
@Composable
fun GameSelectionScreenPreview() {
    TupleNBackApp()
}