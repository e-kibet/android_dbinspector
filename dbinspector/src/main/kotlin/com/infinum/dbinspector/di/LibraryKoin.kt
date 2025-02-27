package com.infinum.dbinspector.di

import android.content.Context
import com.infinum.dbinspector.BuildConfig
import com.infinum.dbinspector.logger.Logger
import com.infinum.dbinspector.ui.Presentation
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.logger.Level
import org.koin.dsl.module

internal object LibraryKoin {

    private val koinApplication = KoinApplication.init()

    fun init(context: Context) {
        koinApplication.apply {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(context)
            modules(Presentation.modules())
        }
    }

    fun koin(): Koin = koinApplication.koin

    fun setLibraryLogger(logger: Logger) {
        koin().loadModules(
            listOf(
                module {
                    single { logger }
                }
            )
        )
    }
}
