package org.squidfish.tuplenback.games.engines

/**
 * Enum of possible Grid Game states
 */
enum class GridPosition {
    NONE,
    UP_LEFT,
    UP,
    UP_RIGHT,
    LEFT,
    CENTER,
    RIGHT,
    DOWN_LEFT,
    DOWN,
    DOWN_RIGHT,
}

class GridEngine(recallsBack: Int, repeatChance: Int) : GameEngine(recallsBack, repeatChance) {
    override val gameButtonText = "Grid"

    // generates random grid position
    override fun genNewMnemonic(forbidden: List<Int>): Int = GridPosition.entries.filterNot {
        it == GridPosition.NONE || forbidden.contains(it.ordinal)
    }.random().ordinal

    override fun getRecent(): Int = super.getRecent() ?: GridPosition.NONE.ordinal
}
