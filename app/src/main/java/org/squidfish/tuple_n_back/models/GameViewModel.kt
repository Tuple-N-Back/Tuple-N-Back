package org.squidfish.tuple_n_back.models

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import org.squidfish.tuple_n_back.NQueue

abstract class GameViewModel(gameModel: GameModel) : ViewModel() {
    abstract val gameButtonText: String

    private val queue by mutableStateOf(NQueue<Int>(gameModel.recallsBack))
    var isRepeat by mutableStateOf(false)

    var forceRecomposition by mutableStateOf(false)

    protected abstract fun genNewState(): Int
    open fun invokeChange(context: Context) {
        forceRecomposition = true
    }

    fun createNewState() {
        val state = genNewState()

        // can't have repeats when we haven't reached turn n
        if (queue.isFull()) {
            isRepeat = (state == queue.dequeue())
        }

        queue.enqueue(state)
    }

    open fun getRecent() : Int? {
        return queue.getFirst()
    }
}