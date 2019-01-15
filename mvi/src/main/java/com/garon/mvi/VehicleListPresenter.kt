package com.garon.mvi

import com.garon.mvi.VehiclesIntent.*
import com.garon.mvi.common.BasePresenter
import com.garon.mvi.di.VehicleListScope
import io.reactivex.functions.BiFunction
import javax.inject.Inject

@VehicleListScope
class VehicleListPresenter @Inject constructor(
    interactor: VehicleListInteractor
) : BasePresenter<VehiclesIntent, VehiclesAction, VehiclesResult, VehiclesState>(interactor) {

    override val defaultState: VehiclesState
        get() = VehiclesState.default()

    override val lastIntention: VehiclesIntent
        get() = LastIntention

    override val stateReducer: BiFunction<VehiclesState, VehiclesResult, VehiclesState>
        get() = BiFunction { prevState, result ->
            when (result) {
                VehiclesResult.Error -> prevState.copy(
                    isLoading = false,
                    isError = true
                )
                is VehiclesResult.InProgress -> prevState.copy(
                    isLoading = true,
                    isError = false
                )
                is VehiclesResult.Success -> prevState.copy(
                    isLoading = false,
                    isError = false,
                    list = result.list
                )
                VehiclesResult.LastResult -> prevState.copy()
            }
        }

    override fun actionFromIntent(intent: VehiclesIntent): VehiclesAction {
        return when (intent) {
            InitialIntent -> VehiclesAction.LoadFirstBatch
            LastIntention -> VehiclesAction.LastAction
            LoadNextIntent -> VehiclesAction.LoadNextBatch
            RefreshIntent -> VehiclesAction.RefreshAction
            is ViewVehicleIntent -> VehiclesAction.ViewVehicleAction(intent.vehicleViewModel)
        }
    }
}
