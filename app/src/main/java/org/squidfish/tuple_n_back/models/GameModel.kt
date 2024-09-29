package org.squidfish.tuple_n_back.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

class GameModel(val recallsBack: Int, val totalRounds: Int, val milliPerRound: Int) {
    var currentRound by mutableIntStateOf(0)
}