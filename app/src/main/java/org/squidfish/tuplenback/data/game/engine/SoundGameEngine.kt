package org.squidfish.tuplenback.data.game.engine

import android.app.Application
import android.media.MediaPlayer
import android.util.Log
import androidx.annotation.RawRes
import org.squidfish.tuplenback.games.engines.GameEngine

class SoundGameEngine(
    private val application: Application,
    private var player: MediaPlayer,
    @field:RawRes private val sounds: List<Int>,
    recallsBack: Int,
    repeatChance: Int,
) : GameEngine(recallsBack, repeatChance) {
    override val gameButtonText = "Sound"

    override fun genNewMnemonic(forbidden: List<Int>): Int = (sounds.indices.toSet() - forbidden).random()

    override fun onNewRound(mnemonicId: Int) {
        playSound(sounds[mnemonicId])
    }

    override fun onGameEnd() {
        player.release()
        super.onGameEnd()
    }

    private fun playSound(@RawRes soundId: Int) {
        Log.d("SoundGameEngine", "Playing sound")

        val asset = application.resources.openRawResourceFd(soundId) ?: return

        if (player.isPlaying) {
            player.stop()
        }
        player.reset()
        player.setDataSource(
            asset.fileDescriptor,
            asset.startOffset,
            asset.declaredLength,
        )
        player.prepare()
    }
}
