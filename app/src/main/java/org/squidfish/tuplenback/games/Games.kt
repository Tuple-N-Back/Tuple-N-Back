package org.squidfish.tuplenback.games

import androidx.annotation.DrawableRes
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.parametersOf
import org.squidfish.tuplenback.R
import org.squidfish.tuplenback.data.game.engine.SoundGameEngine
import org.squidfish.tuplenback.data.game.engine.Vibration
import org.squidfish.tuplenback.data.game.engine.VibrationEngine
import org.squidfish.tuplenback.games.engines.GameEngine
import org.squidfish.tuplenback.games.engines.GridEngine

/**
 * Contains all the necessary game data
 *
 * @see[Game]
 * @see[GameSettings]
 */
data class Level(
    val level: Int,
    val game: Game,
    val settings: GameSettings,
)

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
enum class Game(val modules: List<GameModule>) {
    GridPiano(
        listOf(GameModule.Grid, GameModule.Piano),
    ),
    Grid(
        listOf(GameModule.Grid),
    ),
    GridVibration(
        listOf(GameModule.Grid, GameModule.Vibration),
    ),
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
    Vibration,
    ;

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

    /**
     * Create a new game engine of the current enum entry.
     *
     * @return A newly created game engine.
     */
    fun toGameEngine(settings: GameSettings): GameEngine = when (this) {
        Grid -> GridEngine(settings.recallsBack, settings.repeatChance)
        Piano -> GlobalContext.get().get<SoundGameEngine> {
            parametersOf(
                listOf(
                    R.raw.key01,
                    R.raw.key02,
                    R.raw.key03,
                    R.raw.key04,
                    R.raw.key05,
                    R.raw.key06,
                    R.raw.key07,
                    R.raw.key08,
                    R.raw.key09,
                    R.raw.key10,
                    R.raw.key11,
                    R.raw.key12,
                    R.raw.key13,
                    R.raw.key14,
                    R.raw.key15,
                    R.raw.key16,
                    R.raw.key17,
                    R.raw.key18,
                    R.raw.key19,
                    R.raw.key20,
                    R.raw.key21,
                    R.raw.key22,
                    R.raw.key23,
                    R.raw.key24,
                ),
                settings.recallsBack,
                settings.repeatChance,
            )
        }
        Colour -> TODO()
        Vibration -> GlobalContext.get().get<VibrationEngine> {
            parametersOf(
                (100..1000 step 100).map {
                    Vibration(settings.millisPerRound.toInt()) {
                        vibrate(it)
                    }
                },
                settings.recallsBack,
                settings.repeatChance,
            )
        }
    }
}
