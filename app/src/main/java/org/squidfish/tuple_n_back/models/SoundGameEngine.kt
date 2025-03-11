package org.squidfish.tuple_n_back.models

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.util.Log


class SoundGameEngine(recallsBack: Int, repeatChance: Int) : GameEngine(recallsBack, repeatChance) {
    override val gameButtonText = "Sound"

    override fun genNewMnemonic(forbiddem: List<Int?>): Int {
         return SoundGameEngine.keys.indices.filter { !forbiddem.contains(it) }.random()
    }

    companion object {
        val keys = Array(24) { i ->
            val j = i +1
            if (j < 10) {
                "key0$j"
            } else {
                "key$j"
            }
        }

        private var mp: MediaPlayer? = null

        @SuppressLint("DiscouragedApi")
        fun playSound(context: Context, resourceId: Int) {
            Log.d("SoundGameEngine", "Playing sound")

            val packageName = context.packageName
            val noteId = context.resources.getIdentifier(keys[resourceId], "raw", packageName)

            if (this.mp == null) {
                this.mp = MediaPlayer.create(context, noteId).apply {
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
                        assetFD.declaredLength
                    )
                    prepare()
                }
            }
        }
    }

}