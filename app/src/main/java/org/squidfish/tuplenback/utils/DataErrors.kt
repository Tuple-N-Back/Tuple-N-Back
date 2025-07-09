package org.squidfish.tuplenback.utils

sealed interface DataError : Error

sealed interface ValidationError : DataError

object MissingGameStats : ValidationError
object InconsistentGameStats : ValidationError
object InconsistentGameData : ValidationError
