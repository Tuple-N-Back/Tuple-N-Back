package org.squidfish.tuple_n_back.models

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import org.squidfish.tuple_n_back.MainActivity
import org.squidfish.tuple_n_back.R
import java.io.File
import java.io.FileInputStream


class SoundViewModel(n: Int, val randomSoundsAmount: Int) : GameViewModel<Int>(n) {
    override val TAG = "SoundViewModel"
    var mp: MediaPlayer? = null

    val keys = Array(24) { i ->
        val j = i +1
        if (j < 10) {
            "key0$j"
        } else {
            "key$j"
        }
    }


    override fun genNewState(): Int {
         return (0..keys.size - 1).random()
    }

    @SuppressLint("DiscouragedApi")
    fun invokeChange(context: Context) {
        val recent = getRecent() ?: return

        val packageName = context.packageName
        val noteId = context.resources.getIdentifier(keys[recent], "raw", packageName)

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
                setDataSource(assetFD.fileDescriptor, assetFD.startOffset, assetFD.declaredLength)
                prepare()
            }
        }
    }

}