package com.finepointmobile.myapplication

/**
 * Created by Роман on 17.01.2018.
 */
import android.app.Application
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}