package org.squidfish.tuple_n_back.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MainMenuScreen(
    onSelectGameMode: () -> Unit,
    onPlayRecent: () -> Unit,
    onSettings: () -> Unit,
    onInfo: () -> Unit,
    onExit: () -> Unit,
) {

    Box (modifier = Modifier.padding(start = 32.dp, end = 32.dp).fillMaxHeight(), contentAlignment = Alignment.Center) {
        Column (
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
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        Text(name, style = MaterialTheme.typography.headlineMedium)
    }
}

@Preview
@Composable
fun MainMenuScreenPreview() {
    TupleNBackAppPreview()
}