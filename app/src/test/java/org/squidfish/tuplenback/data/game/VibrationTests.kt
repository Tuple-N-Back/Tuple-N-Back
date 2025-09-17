package org.squidfish.tuplenback.data.game

import kotlin.test.Test
import kotlin.test.assertEquals
import org.squidfish.tuplenback.data.game.engine.Vibration

class VibrationTests {
    @Test
    fun `using consecutive vibrate calls merges them into one vibration`() {
        // Given
        val vibration = Vibration(1000) {
            // When
            vibrate(100)
            vibrate(200)
            vibrate(300)
        }

        // Then
        assertEquals(listOf(0L, 600L), vibration.getTimings())
    }

    @Test
    fun `using consecutive pause calls merges them into one pause`() {
        // Given
        val vibration = Vibration(1000) {
            vibrate(100)

            // When
            pause(100)
            pause(200)
            pause(300)
        }

        // Then
        assertEquals(listOf(0L, 100L, 600L), vibration.getTimings())
    }

    @Test
    fun `vibrate and pause have no effect when entire vibration takes maxTime`() {
        // Given
        val vibration = Vibration(200) {
            vibrate(200)

            // When
            pause(100)
            vibrate(300)
            pause(200)
            vibrate(500)
        }

        // Then
        assertEquals(listOf(0L, 200L), vibration.getTimings())
    }

    @Test
    fun `exceeding maxTime for a vibration adds valid amount to meet maxTime limit`() {
        // Given
        val vibration = Vibration(500) {
            vibrate(200)
            pause(100)

            // When
            vibrate(500)
        }

        // Then
        assertEquals(listOf(0L, 200L, 100L, 200L), vibration.getTimings())
    }

    @Test
    fun `delaying a vibration is handled correctly`() {
        // Given
        val vibration = Vibration(1000) {
            // When
            pause(500)
            vibrate(200)
        }

        // Then
        assertEquals(listOf(500L, 200L), vibration.getTimings())
    }
}
