package org.squidfish.tuplenback.di

import android.media.MediaPlayer
import org.koin.dsl.module
import org.squidfish.tuplenback.data.game.engine.SoundGameEngine
import org.squidfish.tuplenback.data.game.engine.VibrationEngine

val dynamicModule = module {
    factory {
        VibrationEngine(
            vibrator = get(),
            vibrations = it.get(),
            recallsBack = it[1],
            repeatChance = it[2],
        )
    }
    factory {
        SoundGameEngine(
            application = get(),
            player = MediaPlayer().apply {
                setOnPreparedListener { start() }
                setOnCompletionListener { reset() }
            },
            sounds = it.get(),
            recallsBack = it[1],
            repeatChance = it[2],
        )
    }
}
