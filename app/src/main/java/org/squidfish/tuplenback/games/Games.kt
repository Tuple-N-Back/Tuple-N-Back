package org.squidfish.tuplenback.games

import androidx.annotation.DrawableRes
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.parametersOf
import org.squidfish.tuplenback.R
import org.squidfish.tuplenback.data.game.Vibration
import org.squidfish.tuplenback.games.engines.GameEngine
import org.squidfish.tuplenback.games.engines.GridEngine
import org.squidfish.tuplenback.games.engines.SoundGameEngine

/**
 * Each enum value is to be a separate game. A game needs list of modules and settings
 *
 * Note that the repeat chance is passed to all modules, that means, for a game with 2 modules and
 * 50% repeat chance, the first module will have 50% chance to repeat per round and so will the
 * second.
 *
 * @see[GameModule]
 * @see[GameSettings]
 */
enum class Game(val modules: List<GameModule>, val settings: GameSettings) {
    GridPiano(listOf(GameModule.Grid, GameModule.Piano), GameSettings(2, 3, 1500, 15)),
    Grid(listOf(GameModule.Grid), GameSettings(2, 3, 1500, 20)),
    GridVibration(listOf(GameModule.Grid, GameModule.Vibration), GameSettings(2, 3, 1500, 15)),
}

/**
 * Enum of game modules. A [Game] consists of at least one module. Each module needs a [GameEngine]
 * and a composable function to work.
 *
 * @see[GameEngine]
 */
enum class GameModule {
    Grid,
    Piano,
    Colour,
    Vibration;

    /**
     * Create a new game engine of the current enum entry.
     *
     * @return A newly created game engine.
     */
    fun toGameEngine(recallsBack: Int, repeatChance: Int): GameEngine = when (this) {
        Grid -> GridEngine(recallsBack, repeatChance)
        Piano -> SoundGameEngine(recallsBack, repeatChance)
        Colour -> TODO()
        Vibration -> GlobalContext.get().get {
            parametersOf(
                (100..1000).map {
                    Vibration(1) {
                        vibration(it)
                    }
                },
                recallsBack,
                repeatChance,
            )
        }
    }

    /**
     * Get the icon for the current enum entry
     *
     * @return The game module icon.
     *
     * TODO: might be better to include this with the enum. That, or remove the string res from the
     *  enums and make a toString function
     */
    val gameIconRes: Int
        @DrawableRes get() = when (this) {
            Grid -> R.drawable.grid_game_icon
            Piano -> R.drawable.piano_game_icon
            Colour -> TODO()
            Vibration -> R.drawable.vibration_svgrepo_com
        }
}
