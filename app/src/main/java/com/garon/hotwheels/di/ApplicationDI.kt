package com.garon.hotwheels.di

import android.app.Application
import android.content.Context
import com.garon.hotwheels.HotWheelApplication
import com.garon.hotwheels.list.di.VehicleListComponent
import com.garon.mvi.VehicleListApi
import com.garon.mvi.utils.IoScheduler
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class])
interface ApplicationComponent {

    fun inject(app: HotWheelApplication)

    fun componentBuilder(): VehicleListComponent.Builder

    @Component.Builder
    interface Builder {

        fun build(): ApplicationComponent

        @BindsInstance
        fun application(app: Application): Builder
    }
}

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context = application.applicationContext
}

@Module
class NetworkModule {

    @Provides
    @Singleton
    @IoScheduler
    fun providesIoScheduler(): Scheduler = Schedulers.io()

    @Provides
    @Singleton
    fun providesRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("http://private-6d86b9-vehicles5.apiary-mock.com")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

    @Provides
    fun providesVehicleListApi(retrofit: Retrofit): VehicleListApi {
        return retrofit.create(VehicleListApi::class.java)
    }
}
