package org.squidfish.tuplenback.data.game

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import kotlin.math.min
import org.squidfish.tuplenback.games.engines.GameEngine

class Vibration private constructor(
    private var timeLeftMillis: Int,
    private val timings: MutableList<Int> = mutableListOf(0),
) {
    constructor(maxTimeMillis: Int, config: Vibration.() -> Unit) : this(maxTimeMillis) {
        config()
    }

    /**
     * Returns a list of Long values representing a vibration pattern.
     * The list contains alternating durations: the first Long is the delay (pause) duration,
     * the second is the vibration duration, the third is the pause duration, and so on.
     * All duration values are in milliseconds.
     *
     * @return A list of Longs where elements at even indices (0, 2, ...) are pause durations (in ms)
     *         and elements at odd indices (1, 3, ...) are vibration durations (in ms).
     */
    fun getTimings(): List<Long> = timings.map { it.toLong() }

    /**
     * Adds a vibration segment to the current vibration pattern.
     *
     * If the last added segment was a pause, or if this is the first segment, a new vibration duration is added.
     * If the last added segment was a vibration, this duration is appended to the existing vibration segment.
     * The total duration added will not make the pattern exceed `maxTimeMillis`.
     *
     * @param timeMillis The desired duration of the vibration in milliseconds.
     *                   This value will be clamped if the pattern will exceed `maxTimeMillis`.
     */
    fun vibration(timeMillis: Int) {
        if (timeLeftMillis == 0) return

        val timeToAdd = min(timeMillis, timeLeftMillis)

        if (timings.size % 2 == 0) {
            timings.add(timings.removeAt(timings.lastIndex) + timeToAdd)
        } else {
            timings.add(timeToAdd)
        }

        timeLeftMillis -= timeToAdd
    }

    /**
     * Adds a pause segment to the current vibration pattern.
     *
     * If the last added segment was a vibration, a new pause duration is added.
     * If the last added segment was a pause, this duration is added to the existing pause segment.
     * The total duration added will not make the pattern exceed `maxTimeMillis`.
     *
     * @param timeMillis The desired duration of the pause in milliseconds.
     *                   This value will be clamped if the pattern will exceed `maxTimeMillis`.
     */
    fun pause(timeMillis: Int) {
        if (timeLeftMillis <= 0) return

        val timeToAdd = min(timeMillis, timeLeftMillis)

        if (timings.size % 2 == 0) {
            timings.add(timeToAdd)
        } else {
            timings.add(timings.removeAt(timings.lastIndex) + timeToAdd)
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
    override val gameButtonText: String = "Vibration"

    override fun genNewMnemonic(forbidden: List<Int>): Int = (vibrations.indices.toSet() - forbidden).random()

    override fun onNewRound(mnemonicId: Int) = playVibration(vibrations[mnemonicId])

    private fun playVibration(vibration: Vibration) {
        val pattern = longArrayOf(*vibration.getTimings().toLongArray(), 0, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createWaveform(pattern, -1), // -1 == no repeat
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, -1)
        }
    }
}
