package org.squidfish.tuplenback

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.squidfish.tuplenback.di.repositoryModule
import org.squidfish.tuplenback.di.viewModelModule

class TupleNBackApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TupleNBackApplication)
            modules(
                repositoryModule,
                viewModelModule
            )
        }
    }
}
