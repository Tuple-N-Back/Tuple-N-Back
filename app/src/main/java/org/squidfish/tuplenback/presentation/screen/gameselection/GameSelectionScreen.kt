package org.squidfish.tuplenback.presentation.screen.gameselection

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import org.squidfish.tuplenback.games.Game
import org.squidfish.tuplenback.presentation.util.ObserveAsEvents
import org.squidfish.tuplenback.views.ScreenType

@Composable
fun GameSelectionScreen(viewModel: GameSelectionViewModel, navController: NavController) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.eventFlow) { event ->
        when (event) {
            is GameSelectionEvent.StartGame -> navController.navigate(ScreenType.Game.name)
        }
    }

    GameSelectionScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}

@Composable
fun GameSelectionScreen(
    state: GameSelectionState,
    onAction: (GameSelectionAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
    ) {
        items(state.games, key = { it.title }) { game ->
            GameCard(
                game = game,
                onClick = { onAction(GameSelectionAction.GameSelected(game)) },
            )
        }
    }
}

@Composable
fun GameCard(
    game: GameModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = false,
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }

    // on-click open pop-up
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = game.title,
                    style = MaterialTheme.typography.displaySmall,
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
                    modifier = Modifier.weight(1f),
                ) {
                    game.icons.forEach { iconRes ->
                        Icon(
                            imageVector = ImageVector.vectorResource(iconRes),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                        )
                    }
                }

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "collapse" else "expand",
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
            AnimatedVisibility(visible = expanded) {
                Column {
                    Text(
                        text = game.highestLevel,
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    Text(
                        text = game.totalGames,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@PreviewScreenSizes
@Composable
private fun GameSelectionScreenPreview() {
    LazyColumn(
        modifier = Modifier.systemBarsPadding(),
    ) {
        items(Game.entries) { game ->
            GameCard(
                game = game.asModel,
                onClick = {},
            )
        }
        items(Game.entries) { game ->
            GameCard(
                game = game.asModel,
                onClick = {},
                initiallyExpanded = true,
            )
        }
    }
}
