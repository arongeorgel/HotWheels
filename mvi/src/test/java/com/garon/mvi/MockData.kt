package com.garon.mvi

val successResponse1 = VehicleApiListModel(
    currentPage = 1,
    nextPage = 2,
    totalPages = 10,
    count = 5,
    vehicles = listOf(
        VehicleModel(
            vehicleId = 107623,
            vrn = "PARK01",
            country = "GB",
            color = "Blue",
            type = "Truck",
            default = true
        ),
        VehicleModel(
            vehicleId = 107523,
            vrn = "PARK02",
            country = "ES",
            color = "White",
            type = "RV",
            default = false
        )
    )
)

val successResponse2 = VehicleApiListModel(
    currentPage = 2,
    nextPage = 3,
    totalPages = 10,
    count = 5,
    vehicles = listOf(
        VehicleModel(
            vehicleId = 207623,
            vrn = "PARK03",
            country = "GB",
            color = "Blue",
            type = "Car",
            default = true
        ),
        VehicleModel(
            vehicleId = 207523,
            vrn = "PARK04",
            country = "ES",
            color = "White",
            type = "RV",
            default = false
        )
    )
)
