package org.squidfish.tuplenback.games.engines

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.util.Log

class SoundGameEngine(recallsBack: Int, repeatChance: Int) : GameEngine(recallsBack, repeatChance) {
    override val gameButtonText = "Sound"

    override fun genNewMnemonic(forbidden: List<Int>): Int = keys.indices.filterNot { forbidden.contains(it) }.random()

    override fun onNewRound(mnemonicId: Int) {}

    companion object {
        val keys = Array(24) { i ->
            val j = i + 1
            if (j < 10) "key0$j" else "key$j"
        }

        private var mp: MediaPlayer? = null

        @SuppressLint("DiscouragedApi")
        fun playSound(context: Context, resourceId: Int) {
            Log.d("SoundGameEngine", "Playing sound")

            val packageName = context.packageName
            val noteId = context.resources.getIdentifier(keys[resourceId], "raw", packageName)

            if (mp == null) {
                mp = MediaPlayer.create(context, noteId).apply {
                    setOnPreparedListener { start() }
                    setOnCompletionListener { reset() }
                }
            } else {
                if (mp?.isPlaying == true) {
                    mp?.stop()
                }

                val assetFD = context.resources.openRawResourceFd(noteId) ?: return
                mp?.run {
                    reset()
                    setDataSource(
                        assetFD.fileDescriptor,
                        assetFD.startOffset,
                        assetFD.declaredLength,
                    )
                    prepare()
                }
            }
        }
    }
}
