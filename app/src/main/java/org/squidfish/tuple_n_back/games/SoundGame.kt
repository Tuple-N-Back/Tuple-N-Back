package org.squidfish.tuple_n_back.games

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import org.squidfish.tuple_n_back.models.GridPosition
import org.squidfish.tuple_n_back.models.SoundGameEngine

@Composable
fun SoundGame(recordingId: Int) {
    if (!LocalInspectionMode.current) { // because media player preview problem
        SoundGameEngine.playSound(LocalContext.current, recordingId)
    }
}
@Preview
@Composable
fun SoundGamePreview() {
}
