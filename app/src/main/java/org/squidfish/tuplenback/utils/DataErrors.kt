package org.squidfish.tuplenback.utils

sealed interface DataError : Error

sealed interface ValidationError : DataError {
    object MissingGameStats : ValidationError
    object InconsistentGameStats : ValidationError
    object InconsistentGameData : ValidationError
}

sealed interface LevelDeserializationError : ValidationError {
    object ConfigNotFound : LevelDeserializationError
    object InvalidRecallsBack : LevelDeserializationError
    object TooFewRounds : LevelDeserializationError
    object InvalidRoundTime : LevelDeserializationError
    object InvalidRepeatChance : LevelDeserializationError
    object LevelMismatch : LevelDeserializationError // Unordered levels or invalid level ids in json config
    object NonexistentLevel : LevelDeserializationError
    object InvalidSyntax : LevelDeserializationError
}
