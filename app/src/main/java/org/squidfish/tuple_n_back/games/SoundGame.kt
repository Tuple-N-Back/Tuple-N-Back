package org.squidfish.tuple_n_back.games

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import org.squidfish.tuple_n_back.models.SoundGameEngine

@Composable
fun SoundGame(recordingId: Int) {
    SoundGameEngine.playSound(LocalContext.current, recordingId)
}