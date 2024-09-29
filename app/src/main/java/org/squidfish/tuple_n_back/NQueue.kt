package org.squidfish.tuple_n_back

import java.util.LinkedList

class NQueue<T>(private val maxSize: Int) {
    private var queue: LinkedList<T> = LinkedList<T>()

    fun enqueue(element: T) {
        if (queue.size >= maxSize) {
            throw QueueFullException("Queue is full, cannot enqueue")
        }

        queue.addFirst(element)
    }

    fun dequeue(): T {
        if (queue.size == 0) {
            throw QueueEmptyException("Queue is empty, cannot dequeue")
        }

        return queue.removeLast()
    }

    fun isFull(): Boolean {
        return (queue.size == maxSize)
    }

    fun getFirst(): T? {
        if (queue.size == 0) {
            return null
        }

        return queue.first
    }
}

class QueueFullException(message: String): Exception(message)
class QueueEmptyException(message: String): Exception(message)
