package org.squidfish.tuple_n_back.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import org.squidfish.tuple_n_back.NQueue

abstract class GameViewModel<T>(val n: Int) : ViewModel() {
    var queue by mutableStateOf(NQueue<T>(n))
    var isRepeat by mutableStateOf(false)

    abstract val TAG: String

    protected abstract fun genNewState(): T;

    fun addNewState() {
        val state = genNewState()

        // can't have repeats, when we haven't reached turn n
        if (queue.isFull()) {
            isRepeat = (state == queue.dequeue())
        }

        queue.enqueue(state)
    }
}