package org.squidfish.tuplenback.presentation.screen.gameselection

import org.squidfish.tuplenback.games.Game

val Game.asModel
    get() = GameModel(
        title = modules.joinToString(" "),
        icons = modules.map { it.toGameIconRes() },
        // TODO: fetch highestLvl and totalGames
        highestLevel = "Level: 3",
        totalGames = "Games: 10",
    )
