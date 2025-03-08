package org.squidfish.tuple_n_back.models

data class GameStats(
    val name: String,
    val correctRecalls: Int,
    val incorrectRecalls: Int,
    val missedRecalls: Int,
)
