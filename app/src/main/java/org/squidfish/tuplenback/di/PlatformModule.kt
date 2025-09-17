package org.squidfish.tuplenback.di

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class PlatformModule {
    @Single
    fun providesVibrator(application: Application): Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        manager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        application.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
}
