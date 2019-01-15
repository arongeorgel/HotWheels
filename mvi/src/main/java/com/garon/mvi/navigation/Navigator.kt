package com.garon.mvi.navigation

import com.garon.mvi.VehicleViewModel

interface Navigator

interface VehicleListNavigator : Navigator {

    fun goToVehicleDetails(vehicle: VehicleViewModel)
}
