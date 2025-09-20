package org.squidfish.tuplenback.presentation.screen.gameselection

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import org.squidfish.tuplenback.presentation.navigation.LevelData
import org.squidfish.tuplenback.data.game.levels.LevelRepository
import org.squidfish.tuplenback.games.Game
import org.squidfish.tuplenback.games.GameSettings
import org.squidfish.tuplenback.games.Level
import org.squidfish.tuplenback.presentation.navigation.ScreenDestination
import org.squidfish.tuplenback.presentation.util.ObserveAsEvents

@Composable
fun GameSelectionScreen(viewModel: GameSelectionViewModel, navController: NavController) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.eventFlow) { event ->
        when (event) {
            is GameSelectionEvent.StartGame -> {
                // Temporary solution until we rework Game
                val game = Game.entries.first { it.asModel == event.game }

                // TODO: use selected level
                navController.navigate(ScreenDestination.GameScreen(LevelData(game, 0)))
            }
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
    val screenWidth = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.width.toDp() }
    val columnCount = remember(screenWidth) {
        maxOf(1, (screenWidth / 400.dp).toInt())
    }
    val columns = remember(state.games, columnCount) {
        List(columnCount) { mutableListOf<GameModel>() }.also { columns ->
            state.games.forEachIndexed { index, game ->
                columns[index % columnCount].add(game)
            }
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    ) {
        columns.forEach { columnItems ->
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .weight(1f),
            ) {
                columnItems.forEach { game ->
                    GameCard(
                        game = game,
                        onClick = { onAction(GameSelectionAction.GameSelected(game)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun GameCard(
    game: GameModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = false,
) {
    var expanded by rememberSaveable { mutableStateOf(initiallyExpanded) }

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
                BasicText(
                    text = game.title,
                    style = MaterialTheme.typography.displaySmall,
                    maxLines = 1,
                    autoSize = TextAutoSize.StepBased(
                        minFontSize = 16.sp,
                        maxFontSize = 32.sp,
                    ),
                    modifier = Modifier.weight(1f),
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    game.icons.forEach { iconRes ->
                        Icon(
                            imageVector = ImageVector.vectorResource(iconRes),
                            contentDescription = null,
                            modifier = Modifier.requiredSize(40.dp),
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

@PreviewScreenSizes
@PreviewFontScale
@PreviewLightDark
@PreviewDynamicColors
@Composable
private fun GameSelectionScreenPreview() {
    GameSelectionScreen(
        state = GameSelectionState(games = Game.entries.map { it.asModel }),
        onAction = {},
        modifier = Modifier.systemBarsPadding(),
    )
}

@PreviewScreenSizes
@PreviewFontScale
@PreviewLightDark
@PreviewDynamicColors
@Composable
private fun GameCardPreview() {
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
