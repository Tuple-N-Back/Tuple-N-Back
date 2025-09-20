package org.squidfish.tuplenback

import java.util.LinkedList

/**
 * A queue data structure that can only have [maxSize] elements enqueued at a time
 *
 * @param[maxSize] The maximum amount of elements the queue can have.
 */
class NQueue<T>(private val maxSize: Int) {
    private var queue: LinkedList<T> = LinkedList<T>()

    /** Add an element to the queue */
    fun enqueue(element: T) {
        if (queue.size >= maxSize) {
            throw QueueException.FullQueue("Queue is full, cannot enqueue")
        }

        queue.addFirst(element)
    }

    /** Remove the least recent queue element and return it*/
    fun dequeue(): T {
        if (queue.size == 0) {
            throw QueueException.EmptyQueue("Queue is empty, cannot dequeue")
        }

        return queue.removeLast()
    }

    /** Does the queue have [maxSize] elements */
    fun isFull(): Boolean = queue.size == maxSize

    /** get the recent-most queue element */
    fun getFirst(): T? = queue.firstOrNull()

    /** Get the least recent queue element */
    fun getLast(): T? = queue.lastOrNull()
}

sealed class QueueException(message: String): Exception(message) {
    class FullQueue(message: String) : QueueException(message)
    class EmptyQueue(message: String) : QueueException(message)
}
