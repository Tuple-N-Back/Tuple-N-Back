package org.squidfish.tuplenback.data.game

import kotlin.test.Test
import kotlin.test.assertEquals

class VibrationTests {
    @Test
    fun `using consecutive vibration multiple times merges them into one vibration`() {
        // Given
        val vibration = Vibration(1000) {
            // When
            vibration(100)
            vibration(200)
            vibration(300)
        }

        // Then
        assertEquals(listOf(600L), vibration.getTimings())
    }

    @Test
    fun `using consecutive pause multiple times merges them into one vibration`() {
        // Given
        val vibration = Vibration(1000) {
            vibration(100)

            // When
            pause(100)
            pause(200)
            pause(300)
        }

        // Then
        assertEquals(listOf(100L, 600L), vibration.getTimings())
    }

    @Test
    fun `vibration and pause have no effect when entire vibration takes maxTime`() {
        // Given
        val vibration = Vibration(200) {
            vibration(200)

            // When
            pause(100)
            vibration(300)
            pause(200)
            vibration(500)
        }

        // Then
        assertEquals(listOf(200L), vibration.getTimings())
    }

    @Test
    fun `exceeding maxTime for a vibration adds valid amount to meet maxTime limit`() {
        // Given
        val vibration = Vibration(500) {
            vibration(200)
            pause(100)

            // When
            vibration(500)
        }

        // Then
        assertEquals(listOf(200L, 100L, 200L), vibration.getTimings())
    }
}
