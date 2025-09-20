package org.squidfish.tuplenback.utils

sealed interface DataError : Error

sealed interface ValidationError : DataError {
    object MissingGameStats : ValidationError
    object InconsistentGameStats : ValidationError
    object InconsistentGameData : ValidationError
    object MissingLevel : ValidationError
}

sealed interface LevelDeserializationError : ValidationError {
    object MissingConfig : LevelDeserializationError
    object InvalidRecallsBack : LevelDeserializationError
    object TooFewRounds : LevelDeserializationError
    object InvalidRoundTime : LevelDeserializationError
    object InvalidRepeatChance : LevelDeserializationError
    object LevelMismatch : LevelDeserializationError // Unordered levels json config
    object NonexistentLevel : LevelDeserializationError
    object IncompleteCache : LevelDeserializationError
}
