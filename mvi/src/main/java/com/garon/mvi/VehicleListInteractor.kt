package com.garon.mvi

import com.garon.mvi.common.BaseInteractor
import com.garon.mvi.di.VehicleListScope
import com.garon.mvi.navigation.VehicleListNavigator
import com.garon.mvi.utils.IoScheduler
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Scheduler
import timber.log.Timber
import javax.inject.Inject

@VehicleListScope
class VehicleListInteractor @Inject constructor(
    private val useCase: GetVehicleListUseCase,
    private val navigator: VehicleListNavigator,
    @IoScheduler private val scheduler: Scheduler
) : BaseInteractor<VehiclesAction, VehiclesResult>() {

    private var currentPage = 1
    private var totalPages = -1

    private fun checkForNextPage() {
        if (currentPage < totalPages) currentPage++
    }

    private val loadApiProcessor = ObservableTransformer<VehiclesAction, VehiclesResult> { action ->
        action.switchMap {
            val api = useCase.execute(currentPage).share()

            val success = api.filter { apiResult -> apiResult is VehicleApiListModel }
                .map { apiResult -> apiResult as VehicleApiListModel }
                .map { apiModel ->
                    totalPages = apiModel.totalPages
                    checkForNextPage()

                    val vehicleList = mutableListOf<VehicleViewModel>()
                    apiModel.vehicles.map { vehicle ->
                        vehicleList.add(
                            VehicleViewModel(
                                registrationCountry = vehicle.country,
                                registrationNumber = vehicle.vrn,
                                color = VehicleViewModel.Color.fromString(vehicle.color),
                                type = VehicleViewModel.Type.fromString(vehicle.type)
                            )
                        )
                    }

                    VehiclesResult.Success(list = vehicleList)
                }

            val error = api.filter { apiResult -> apiResult is VehicleApiError }
                .map { apiResult -> apiResult as VehicleApiError }
                .map { apiError ->
                    Timber.w("Vehicles API failed with message ${apiError.devErrorMessage} and code ${apiError.httpErrorCode}")
                    VehiclesResult.Error
                }
            Observable.merge(success, error).startWith(VehiclesResult.InProgress)
        }
    }

    private val navigationProcessor: ObservableTransformer<VehiclesAction.ViewVehicleAction, VehiclesResult> =
        ObservableTransformer {
            it.flatMapCompletable {
                Completable.fromAction { navigator.goToVehicleDetails(it.vehicleViewModel) }
            }.toObservable()
        }

    override fun actionsProcessor() =
        ObservableTransformer<VehiclesAction, VehiclesResult> { actions ->
            actions
                .observeOn(scheduler)
                .publish { shared ->
                    Observable.mergeArray(
                        shared.ofType(VehiclesAction.LoadFirstBatch::class.java)
                            .compose(loadApiProcessor),
                        shared.ofType(VehiclesAction.LoadNextBatch::class.java)
                            .compose(loadApiProcessor),
                        shared.ofType(VehiclesAction.LastAction::class.java)
                            .map { VehiclesResult.LastResult },
                        shared.ofType(VehiclesAction.RefreshAction::class.java)
                            .doOnNext {
                                currentPage = 1
                                totalPages = -1
                            }
                            .compose(loadApiProcessor),
                        shared.ofType(VehiclesAction.ViewVehicleAction::class.java)
                            .compose(navigationProcessor)
                    )
                }
        }
}
