package org.squidfish.tuplenback.utils

interface Error

object UnsetGame : Error
object MissingGameModules : Error

object MissingGameStats: Error
object InconsistentGameStats : Error
object InconsistentGameData: Error
