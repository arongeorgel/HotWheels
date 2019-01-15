package com.garon.hotwheels

import androidx.annotation.ColorRes
import com.garon.mvi.VehicleViewModel
import java.util.*

fun Locale.getFlag(): String {
    val flagOffset = 0x1F1E6
    val asciiOffset = 0x41

    val firstChar = Character.codePointAt(country, 0) - asciiOffset + flagOffset
    val secondChar = Character.codePointAt(country, 1) - asciiOffset + flagOffset

    return String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
}

@ColorRes
fun VehicleViewModel.getVehicleColorResource(): Int = when (color) {
    VehicleViewModel.Color.BLUE -> R.color.river
    // TODO Find a better UI to represent white color
    VehicleViewModel.Color.WHITE -> R.color.colorPrimary
    VehicleViewModel.Color.RED -> R.color.colorAccent
    VehicleViewModel.Color.BLACK -> R.color.black
    VehicleViewModel.Color.GREEN -> R.color.colorPrimary
}