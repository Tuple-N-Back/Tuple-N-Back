package org.squidfish.tuplenback.games.engines

import kotlin.random.Random
import org.squidfish.tuplenback.NQueue
import org.squidfish.tuplenback.games.Game
import org.squidfish.tuplenback.models.GameStatsModel

/**
 * Abstract class for common game behaviour, that is, generating new mnemonics, updating the
 * game's state and keeping track of player statistics.
 *
 * @param[recallsBack] Amount of mnemonics the player has to remember.
 * @param[repeatChance] Chance for a generated mnemonic to be a repeat. Must be an int from 0 to 100
 * @property[gameButtonText] How the recall button will be named - should be the game's name
 * @property[queue] Collection containing the recallsBack most recent mnemonics.
 * @property[isRepeat] True if there is a mnemonic repeat, that is, the most recent mnemonic is of
 * the same type as the [recallsBack+1] mnemonic.
 * @property[stats] Player performance statistics.
 *
 * @see[GameStatsModel]
 */
abstract class GameEngine(recallsBack: Int, private val repeatChance: Int) {
    abstract val gameButtonText: String

    // mnemonic queue
    private val queue = NQueue<Int>(recallsBack)
    var isRepeat = false
        private set

    private var stats = GameStatsModel(
        difficulty = recallsBack,
        gameType = Game.None,
    )

    init {
        // TODO: verify constructor parameters. recallsBack must be > 1 and repeatChance in [1,100]
    }

    /**
     * Generate a random game mnemonic i.e. game state.
     * @param[forbidden] mnemonics that will not be generated
     * @return integer representing the mnemonic.
     *
     * @see[createNewState]
     */
    protected abstract fun genNewMnemonic(forbidden: List<Int?> = emptyList()): Int

    /**
     * Determine if the next generated mnemonic should be a repeat
     * @return [repeatChance]% chance of returning True
     */
    private fun shouldRepeat(): Boolean = Random.Default.nextInt(1, 100) <= repeatChance

    /**
     * Adds a new mnemonic to the mnemonic queue. Sets the [isRepeat] flag if a repeat has occurred.
     */
    fun createNewState(): Int {
        val state = if (queue.isFull()) {
            val oldestMnem = queue.dequeue()

            if (shouldRepeat()) {
                isRepeat = true
                oldestMnem
            } else {
                isRepeat = false
                genNewMnemonic(listOf(oldestMnem))
            }
        } else {
            genNewMnemonic(emptyList())
        }

        queue.enqueue(state)
        return state
    }

    /**
     * Update the player's stats for the module..
     *
     * @param[recallGuess] true if the player has made a guessed, that is, they thought a mnemonic
     * repeat happened this round.
     */
    fun updateStats(recallGuess: Boolean) {
        when {
            // guess on repeat
            recallGuess && isRepeat -> stats = stats.copy(correctRecalls = stats.correctRecalls + 1)

            // guess on non-repeat
            recallGuess && !isRepeat -> stats = stats.copy(incorrectRecalls = stats.incorrectRecalls + 1)

            // no guess on repeat
            !recallGuess && isRepeat -> stats = stats.copy(missedRecalls = stats.missedRecalls + 1)
        }
    }

    /**
     * Get the most recently added mnemonic.
     *
     * @return integer representing the mnemonic
     */
    open fun getRecent(): Int? = queue.getFirst()

    /**
     * Get player stats.
     *
     * @return the player's stats for the game module
     */
    fun getStats(): GameStatsModel = stats

    /**
     * Reset the player stats.
     */
    fun resetStats() {
        stats = GameStatsModel(difficulty = stats.difficulty)
    }
}
