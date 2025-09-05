package org.squidfish.tuplenback

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module
import org.squidfish.tuplenback.di.DataSourceModule
import org.squidfish.tuplenback.di.RepositoryModule
import org.squidfish.tuplenback.di.ViewModelModule

class TupleNBackApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@TupleNBackApplication)
            modules(
                DataSourceModule().module,
                RepositoryModule().module,
                ViewModelModule().module,
            )
        }
    }
}
