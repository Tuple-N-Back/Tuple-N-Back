package org.squidfish.tuplenback.data.game

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import kotlin.math.min
import org.squidfish.tuplenback.games.engines.GameEngine

class Vibration private constructor(
    private var timeLeftMillis: Int,
    private val timings: MutableList<Int> = mutableListOf(),
) {
    constructor(maxTimeMillis: Int, config: Vibration.() -> Unit) : this(maxTimeMillis) {
        config()
    }

    fun getTimings(): List<Long> = timings.map { it.toLong() }

    fun vibration(timeMillis: Int) {
        if (timeLeftMillis == 0) return

        val timeToAdd = min(timeMillis, timeLeftMillis)

        if (timings.size % 2 == 0) {
            timings.add(timeToAdd)
        } else {
            timings.add(timings.drop(1).single() + timeToAdd)
        }

        timeLeftMillis == timeToAdd
    }

    fun pause(timeMillis: Int) {
        if (timeLeftMillis <= 0) return
        if (timings.isEmpty()) return

        val timeToAdd = min(timeMillis, timeLeftMillis)

        if (timings.size % 2 == 0) {
            timings.add(timings.drop(1).single() + timeToAdd)
        } else {
            timings.add(timeToAdd)
        }

        timeLeftMillis -= timeToAdd
    }
}

class VibrationEngine(
    private val vibrator: Vibrator,
    private val vibrations: List<Vibration>,
    recallsBack: Int,
    repeatChance: Int,
) : GameEngine(recallsBack, repeatChance) {
    fun playVibration(vibration: Vibration) {
        val pattern = longArrayOf(0, *vibration.getTimings().toLongArray(), 0, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createWaveform(pattern, -1), // -1 == no repeat
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, -1)
        }
    }

    override val gameButtonText: String = "Vibration"

    override fun genNewMnemonic(forbidden: List<Int>): Int = (vibrations.indices.toSet() - forbidden).random()
}
