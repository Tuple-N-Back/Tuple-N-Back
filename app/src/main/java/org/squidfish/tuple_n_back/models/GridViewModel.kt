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
    DOWN_RIGHT
}

class GridViewModel(n: Int) : GameViewModel<GridPosition>(n) {
    override val TAG = "GridViewModel"

    override fun genNewState(): GridPosition {
        return GridPosition.entries.random()
    }

}