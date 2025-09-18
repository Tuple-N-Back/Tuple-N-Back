package org.squidfish.tuplenback.utils

import java.util.logging.Level

sealed interface DataError : Error

sealed interface ValidationError : DataError {
    object MissingGameStats : ValidationError
    object InconsistentGameStats : ValidationError
    object InconsistentGameData : ValidationError
}

sealed interface LevelDeserializationError : ValidationError {
    object InvalidGameType : LevelDeserializationError
    object InvalidRecallsBack : LevelDeserializationError
    object TooFewRounds : LevelDeserializationError
    object InvalidRoundTime : LevelDeserializationError
    object InvalidRepeatChance : LevelDeserializationError
    object LevelMismatch : LevelDeserializationError // Unordered levels json config
}
