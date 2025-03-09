package org.squidfish.tuple_n_back.models

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

class GridEngine(recallsBack: Int) : GameEngine(recallsBack) {
    override val gameButtonText = "Grid"

    // generates random grid position
    override fun genNewMnemonic(): Int {
        return (GridPosition.entries.filter { it != GridPosition.NONE }.random()).ordinal
    }

    override fun getRecent(): Int {
        return super.getRecent() ?: return GridPosition.NONE.ordinal
    }
}