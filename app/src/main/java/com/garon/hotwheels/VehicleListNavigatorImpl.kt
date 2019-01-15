package com.garon.hotwheels

import androidx.fragment.app.FragmentActivity
import com.garon.hotwheels.base.newFragment
import com.garon.hotwheels.details.VehicleDetailsFragment
import com.garon.mvi.VehicleViewModel
import com.garon.mvi.navigation.VehicleListNavigator
import javax.inject.Inject

class VehicleListNavigatorImpl @Inject constructor(
    private val activity: FragmentActivity
) : VehicleListNavigator {

    override fun goToVehicleDetails(vehicle: VehicleViewModel) {
        activity.supportFragmentManager
            .beginTransaction()
            .add(R.id.main_content, newFragment<VehicleViewModel, VehicleDetailsFragment>(vehicle))
            .addToBackStack(VehicleViewModel::class.java.simpleName)
            .commit()
    }

}
