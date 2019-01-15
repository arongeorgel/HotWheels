package com.garon.hotwheels

import android.app.Application
import com.garon.hotwheels.di.ApplicationComponent
import com.garon.hotwheels.di.DaggerApplicationComponent
import timber.log.Timber

class HotWheelApplication : Application() {

    lateinit var appComponent: ApplicationComponent
        private set

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        appComponent = DaggerApplicationComponent
            .builder()
            .application(this)
            .build()
        appComponent.inject(this)
    }
}
