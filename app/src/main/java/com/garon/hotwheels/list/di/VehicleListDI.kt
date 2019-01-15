package com.garon.hotwheels.list.di

import androidx.fragment.app.FragmentActivity
import com.garon.hotwheels.VehicleListNavigatorImpl
import com.garon.hotwheels.list.VehicleListFragment
import com.garon.mvi.di.VehicleListScope
import com.garon.mvi.navigation.VehicleListNavigator
import dagger.*

@VehicleListScope
@Subcomponent(modules = [VehicleListBindsModule::class])
interface VehicleListComponent {

    fun inject(fragment: VehicleListFragment)

    @Subcomponent.Builder
    interface Builder {

        fun build(): VehicleListComponent

        @BindsInstance
        fun activity(it: FragmentActivity): Builder
    }
}

@Module
interface VehicleListBindsModule {

    @VehicleListScope
    @Binds
    fun bindsNavigation(it: VehicleListNavigatorImpl): VehicleListNavigator

}

@Module
class VehicleListProvidesModule {

    @VehicleListScope
    @Provides
    fun providesToNavigationActivity(it: FragmentActivity) = it
}
