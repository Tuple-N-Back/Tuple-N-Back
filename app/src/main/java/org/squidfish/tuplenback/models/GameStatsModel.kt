//package org.squidfish.tuplenback.models
//
//import org.squidfish.tuplenback.models.BaseGameStats
//import org.squidfish.tuplenback.games.Game
//import org.squidfish.tuplenback.games.GameModule
//
///**
// * Player round stats in a game.
// *
// * @param[gameEndTime] System time of when the game has concluded.
// * @param[gameType] Type of [Game] played
// * @param[gameModule] Type of [GameModule] for the other stats
// * @param[difficulty] Game difficulty, that is, how many mnemonics the user must remember
// */
//data class GameStatsModel(
//    override val gameEndTime: Long = System.currentTimeMillis(),
//    override val gameType: Game,
//    override val gameModule: GameModule,
//    override val difficulty: Int,
//    override val correctRecalls: Int = 0,
//    override val incorrectRecalls: Int = 0,
//    override val missedRecalls: Int = 0,
//) : BaseGameStats()
