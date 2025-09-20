package org.squidfish.tuplenback.presentation.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import org.squidfish.tuplenback.games.Game

@Parcelize
data class LevelData(val gameMode: Game, val level: Int) : Parcelable
