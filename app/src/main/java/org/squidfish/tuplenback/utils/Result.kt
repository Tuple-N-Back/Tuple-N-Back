package org.squidfish.tuplenback.utils

private typealias RootError = Error

sealed interface Result<out D, out E: RootError> {
    data class Success<out D>(val data: D): Result<D, Nothing>
    data class Error<out E: RootError>(val error: E): Result<Nothing, E>
}

inline fun <T, E: Error, R> Result<T, E>.map(map: (T) -> R): Result<R, E> = when (this) {
    is Result.Success -> Result.Success(map(data))
    is Result.Error -> Result.Error(error)
}

inline fun <T, E: Error> Result<T, E>.onSuccess(action: (T) -> Unit): Result<T, E> = when (this) {
    is Result.Success -> {
        action(data)
        this
    }
    is Result.Error -> this
}

inline fun <T, E: Error> Result<T, E>.onError(action: (E) -> Unit): Result<T, E> = when (this) {
    is Result.Success -> this
    is Result.Error -> {
        action(error)
        this
    }
}

val <D, E: Error> Result<D, E>.data: D?
    inline get() = when (this) {
        is Result.Success -> data
        is Result.Error -> null
    }
