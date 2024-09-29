package org.squidfish.tuple_n_back.models

enum class GridPosition {
    UP_LEFT,
    UP,
    UP_RIGHT,
    LEFT,
    CENTER,
    RIGHT,
    DOWN_LEFT,
    DOWN,
    DOWN_RIGHT,
    NONE
}

class GridViewModel(n: Int) : GameViewModel<GridPosition>(n) {
    override val TAG = "GridViewModel"

    override fun genNewState(): GridPosition {
        return GridPosition.entries.filter { it != GridPosition.NONE }.random()
    }

    override fun getRecent(): GridPosition {
        return super.getRecent() ?: return GridPosition.NONE
    }

}