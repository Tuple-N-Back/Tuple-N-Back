package org.squidfish.tuple_n_back.models

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import org.squidfish.tuple_n_back.NQueue

abstract class GameViewModel(recallsBack: Int) : ViewModel() {
    abstract val gameButtonText: String
    var forceRecomposition by mutableStateOf(false)

    private val queue by mutableStateOf(NQueue<Int>(recallsBack))
    var isRepeat by mutableStateOf(false)

    var correctRecalls by mutableIntStateOf(0)
    var incorrectRecalls by mutableIntStateOf(0)
    var missedRecalls by mutableIntStateOf(0)

    protected abstract fun genNewState(): Int
    open fun invokeChange(context: Context? = null) {
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

    fun updateStats(recallGuess: Boolean) {
        if (recallGuess && isRepeat) {
            correctRecalls++
        } else if (recallGuess && !isRepeat) {
            incorrectRecalls++
        } else if (!recallGuess && isRepeat) {
            missedRecalls++
        }
    }

    open fun getRecent() : Int? {
        return queue.getFirst()
    }

    fun resetStats() {
        correctRecalls = 0
        incorrectRecalls = 0
        missedRecalls = 0
    }

    fun toStats() : GameStats {
        return GameStats(
            name = gameButtonText,
            correctRecalls = correctRecalls,
            incorrectRecalls = incorrectRecalls,
            missedRecalls = missedRecalls,
        )
    }
}