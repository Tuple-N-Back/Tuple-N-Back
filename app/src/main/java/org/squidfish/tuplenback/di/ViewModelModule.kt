package org.squidfish.tuplenback.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.squidfish.tuplenback.models.GameViewModel

val viewModelModule = module {
    viewModelOf(::GameViewModel)
}
