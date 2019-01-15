package com.garon.mvi

import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class GetVehicleListUseCase @Inject constructor(
    private val vehicleListApi: VehicleListApi
) {
    private val inProgress: AtomicBoolean = AtomicBoolean(false)

    fun execute(page: Int): Observable<VehicleApiResult> {
        if (inProgress.getAndSet(true)) return Observable.empty()

        // TODO - implement correct pagination for the API call
        return vehicleListApi.getVehicleList()
            .doOnNext { inProgress.set(false) }
            .compose(ApiCallTransformer("Ups... failed to get the next batch"))
            .publish { shared ->
                Observable.merge(
                    onSuccess(shared),
                    onError(shared)
                )
            }
    }

    private fun onSuccess(it: Observable<ApiCallStateResult>): Observable<VehicleApiResult> =
        it.ofType(ApiSuccessState::class.java)
            .map {
                it.content as VehicleApiListModel
            }

    private fun onError(it: Observable<ApiCallStateResult>): Observable<VehicleApiResult> =
        it.ofType(ApiErrorState::class.java)
            .map {
                VehicleApiError(
                    devErrorMessage = it.geekErrorMessage,
                    httpErrorCode = it.httpError
                )
            }
}

sealed class VehicleApiResult

interface VehicleListApi {

    @GET("/vehicles")
    fun getVehicleList(): Observable<Response<VehicleApiListModel>>
}

data class VehicleApiListModel(
    val count: Int,
    val vehicles: List<VehicleModel>,
    val currentPage: Int,
    val nextPage: Int,
    val totalPages: Int
) : VehicleApiResult()

data class VehicleModel(
    val vehicleId: Long,
    val vrn: String,
    val country: String,
    val color: String,
    val type: String,
    val default: Boolean
)

data class VehicleApiError(
    val devErrorMessage: String,
    val httpErrorCode: Int
) : VehicleApiResult()
