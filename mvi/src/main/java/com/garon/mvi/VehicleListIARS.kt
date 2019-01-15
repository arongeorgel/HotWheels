package com.garon.mvi

import android.os.Parcelable
import com.garon.mvi.common.InitialIntention
import kotlinx.android.parcel.Parcelize
import timber.log.Timber

sealed class VehiclesIntent {

    object InitialIntent : VehiclesIntent(), InitialIntention
    object LastIntention : VehiclesIntent()
    object RefreshIntent : VehiclesIntent()
    object LoadNextIntent : VehiclesIntent()
    data class ViewVehicleIntent(val vehicleViewModel: VehicleViewModel) : VehiclesIntent()
}

sealed class VehiclesAction {

    object LoadFirstBatch : VehiclesAction()
    object LastAction : VehiclesAction()
    object RefreshAction : VehiclesAction()
    object LoadNextBatch : VehiclesAction()
    data class ViewVehicleAction(val vehicleViewModel: VehicleViewModel) : VehiclesAction()
}

sealed class VehiclesResult {

    object InProgress : VehiclesResult()

    data class Success(
        val list: List<VehicleViewModel>
    ) : VehiclesResult()

    object LastResult : VehiclesResult()

    object Error : VehiclesResult()
}

data class VehiclesState(
    val isLoading: Boolean = false,
    val list: List<VehicleViewModel> = emptyList(),
    val isError: Boolean = false
) {
    companion object {
        fun default() = VehiclesState()
    }
}

@Parcelize
data class VehicleViewModel(
    val registrationCountry: String,
    val registrationNumber: String,
    val color: Color,
    val type: Type
) : Parcelable {
    enum class Type(val type: String) {
        CAR("Car"), TRUCK("Truck"), RV("RV");

        internal companion object {
            fun fromString(value: String): Type {
                for (rel: Type in values()) {
                    if (rel.type == value) {
                        return rel
                    }
                }
                Timber.w("Failed to find the given vehicle type")
                return CAR
            }
        }
    }

    enum class Color(val color: String) {
        BLUE("Blue"), WHITE("White"), RED("Red"), BLACK("Black"), GREEN("Green");

        internal companion object {
            fun fromString(value: String): Color {
                for (rel: Color in Color.values()) {
                    if (rel.color == value) {
                        return rel
                    }
                }
                Timber.w("Failed to find the vehicle color.")
                return Color.GREEN
            }
        }
    }
}
