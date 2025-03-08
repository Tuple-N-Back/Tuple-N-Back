package org.squidfish.tuple_n_back.models

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

class GridViewModel(recallsBack: Int) : GameViewModel(recallsBack) {
    override val gameButtonText = "Grid"

    override fun genNewState(): Int {
        return (GridPosition.entries.filter { it != GridPosition.NONE }.random()).ordinal
    }

    override fun getRecent(): Int {
        return super.getRecent() ?: return GridPosition.NONE.ordinal
    }
}