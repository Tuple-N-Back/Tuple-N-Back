package org.squidfish.tuplenback.utils

sealed interface GameError : Error

sealed interface BadConfigurationError : GameError {
    object UnsetGame : BadConfigurationError
    object UnsetSettings: BadConfigurationError
    object MissingGameModules : BadConfigurationError
}
