package com.garon.hotwheels.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.garon.hotwheels.R
import com.garon.hotwheels.base.BaseFragment
import com.garon.hotwheels.getFlag
import com.garon.hotwheels.getVehicleColorResource
import com.garon.hotwheels.kotterknife.bindView
import com.garon.mvi.VehicleViewModel
import com.garon.mvi.utils.plusAssign
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.disposables.CompositeDisposable
import java.util.*

class VehicleDetailsFragment : BaseFragment<VehicleViewModel>() {

    private val closeIcon: View by bindView(R.id.vehicle_details_close)
    private val colorHeader: View by bindView(R.id.vehicle_details_color_header)
    private val countryLicense: TextView by bindView(R.id.vehicle_details_license_country)
    private val licenseNumber: TextView by bindView(R.id.vehicle_details_license_number)
    private val detailsCountry: TextView by bindView(R.id.vehicle_details_country)
    private val detailsRegNumber: TextView by bindView(R.id.vehicle_details_registration)
    private val detailsType: TextView by bindView(R.id.vehicle_details_type)
    private val detailsColor: TextView by bindView(R.id.vehicle_details_color)

    private val disposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.vehicle_details_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        render(arg)

        disposable += closeIcon.clicks().subscribe { activity?.onBackPressed() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable.clear()
    }

    private fun render(vehicle: VehicleViewModel) {
        context?.let {
            val locale = Locale("", vehicle.registrationCountry)
            colorHeader.setBackgroundColor(ContextCompat.getColor(it, vehicle.getVehicleColorResource()))

            val regCountryWithFlag = String.format("%s\n%s", vehicle.registrationCountry, locale.getFlag())
            countryLicense.text = regCountryWithFlag
            licenseNumber.text = vehicle.registrationNumber
            detailsCountry.text = locale.displayCountry
            detailsRegNumber.text = vehicle.registrationNumber
            detailsType.text = vehicle.type.type
            detailsColor.text = vehicle.color.color
        }
    }
}
