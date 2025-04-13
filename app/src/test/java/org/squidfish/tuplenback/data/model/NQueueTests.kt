package org.squidfish.tuplenback.data.model

import org.junit.Assert.assertEquals
import org.junit.Test
import org.squidfish.tuplenback.NQueue
import org.squidfish.tuplenback.QueueException

class NQueueTests {
    @Test
    fun `enqueue adds an element to the queue`() {
        // Given
        val queue = NQueue<Int>(maxSize = 3)

        // When
        queue.enqueue(17)

        // Then
        assertEquals(queue.getFirst(), 17)
        assertEquals(queue.getLast(), 17)
    }

    @Test
    fun `enqueue throws an error when queue is full`() {
        // Given
        val queue = NQueue<Int>(maxSize = 0)

        try {
            // When
            queue.enqueue(17)
        } catch (exception: QueueException) {
            // Then
            assert(exception is QueueException.FullQueue)
        }
    }

    @Test
    fun `dequeue removes an element from the queue`() {
        // Given
        val queue = NQueue<Int>(maxSize = 3)
        queue.enqueue(17)
        queue.enqueue(19)

        // When
        queue.dequeue()

        // Then
        assertEquals(queue.getFirst(), 19)
        assertEquals(queue.getLast(), 19)
    }

    @Test
    fun `dequeue throws an error when queue is empty`() {
        // Given
        val queue = NQueue<Int>(maxSize = 3)

        try {
            // When
            queue.dequeue()
        } catch (exception: QueueException) {
            // Then
            assert(exception is QueueException.EmptyQueue)
        }
    }

    @Test
    fun `isFull returns true when queue is full`() {
        // Given
        val queue = NQueue<Int>(maxSize = 3)
        queue.enqueue(1)
        queue.enqueue(2)
        queue.enqueue(3)

        // When
        val isFull = queue.isFull()

        // Then
        assertEquals(isFull, true)
    }

    @Test
    fun `isFull returns false when queue is not full`() {
        // Given
        val queue = NQueue<Int>(maxSize = 3)
        queue.enqueue(1)
        queue.enqueue(2)

        // When
        val isFull = queue.isFull()

        // Then
        assertEquals(isFull, false)
    }
}
