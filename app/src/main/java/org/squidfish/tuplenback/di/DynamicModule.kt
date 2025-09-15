package org.squidfish.tuplenback.di

import org.koin.dsl.module
import org.squidfish.tuplenback.data.game.VibrationEngine

val dynamicModule = module {
    factory {
        VibrationEngine(
            vibrator = get(),
            vibrations = it.get(),
            recallsBack = it[1],
            repeatChance = it[2],
        )
    }
}
