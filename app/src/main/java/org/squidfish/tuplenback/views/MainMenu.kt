package org.squidfish.tuplenback.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MainMenuScreen(
    onSelectGameMode: () -> Unit,
    onPlayRecent: () -> Unit,
    onSettings: () -> Unit,
    onInfo: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.padding(horizontal = 32.dp).fillMaxHeight(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            MainMenuButton(onSelectGameMode, "Select game mode")
            MainMenuButton(onPlayRecent, "Play recent")
            MainMenuButton(onSettings, "Settings")
            MainMenuButton(onInfo, "Info")
            MainMenuButton(onExit, "Exit")
        }
    }
}

@Composable
fun MainMenuButton(onClick: () -> Unit, name: String) {
    Button(
        onClick = onClick,
        shape = RectangleShape,
        modifier = Modifier.fillMaxWidth().padding(8.dp),
    ) {
        Text(name, style = MaterialTheme.typography.headlineMedium)
    }
}

@Preview
@Composable
fun MainMenuScreenPreview() {
    MainMenuScreen(
        onSelectGameMode = {},
        onPlayRecent = {},
        onSettings = {},
        onInfo = {},
        onExit = {},
        modifier = Modifier,
    )
}
