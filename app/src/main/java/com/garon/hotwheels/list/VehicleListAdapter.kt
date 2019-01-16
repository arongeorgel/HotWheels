package com.garon.hotwheels.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.garon.hotwheels.R
import com.garon.hotwheels.getFlag
import com.garon.hotwheels.getVehicleColorResource
import com.garon.hotwheels.kotterknife.bindView
import com.garon.mvi.VehicleViewModel
import timber.log.Timber
import java.util.*

// TODO update this adapter in order to use delegate pattern
class VehicleListAdapter constructor(
    private val vehicleClick: OnVehicleClick
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_ITEM = 0x0
        const val TYPE_LOADING = 0x1
    }

    private var showLoader: Boolean = false
    private val list: ArrayList<VehicleViewModel> = ArrayList()

    override fun getItemViewType(position: Int): Int =
        if (showLoader && position == list.size) TYPE_LOADING else TYPE_ITEM

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_LOADING -> LoadingViewHolder(
                inflater.inflate(R.layout.car_item_layout, parent, false)
            )
            TYPE_ITEM -> CarViewHolder(
                inflater.inflate(R.layout.car_item_layout, parent, false)
            )
            else -> throw RuntimeException("Unknown view type $viewType")
        }
    }

    override fun getItemCount(): Int = if (showLoader) list.size + 1 else list.size

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (showLoader && position == list.size) return

        val vehicle = list[position]
        val holder = viewHolder as CarViewHolder
        val context = holder.itemView.context

        holder.itemView.setOnClickListener { vehicleClick.invoke(vehicle) }

        when (vehicle.type) {
            VehicleViewModel.Type.CAR -> holder.vehicleIcon.setImageResource(R.drawable.ic_car)
            VehicleViewModel.Type.TRUCK -> holder.vehicleIcon.setImageResource(R.drawable.ic_truck)
            VehicleViewModel.Type.RV -> holder.vehicleIcon.setImageResource(R.drawable.ic_rv)
        }

        val background = ContextCompat.getDrawable(context, R.drawable.shape_circle_accent_color)
        background?.setTint(ContextCompat.getColor(context, vehicle.getVehicleColorResource()))
        holder.vehicleIcon.background = background

        holder.vehicleRegistrationNumber.text = vehicle.registrationNumber

        val locale = Locale("", vehicle.registrationCountry)
        val regCountry =
            context.getString(R.string.vehicle_registration_country, locale.displayCountry, locale.getFlag())
        holder.vehicleRegistrationCountry.text = regCountry

        if (!showLoader && position == list.size) {
            Timber.e("pos: $position, size: ${list.size}")
            holder.separator.visibility = View.GONE
        } else {
            holder.separator.visibility = View.VISIBLE
        }
    }

    fun updateData(vehicles: List<VehicleViewModel>, isRefreshed: Boolean) {
        if(isRefreshed) list.clear()
        list.addAll(vehicles)
        notifyDataSetChanged()
    }

    class CarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val vehicleIcon: ImageView by bindView(R.id.vehicle_item_icon)
        val vehicleRegistrationNumber: TextView by bindView(R.id.vehicle_registration_number)
        val vehicleRegistrationCountry: TextView by bindView(R.id.vehicle_registration_country)
        val separator: View by bindView(R.id.vehicle_item_separator)
    }

    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}

typealias OnVehicleClick = (vehicle: VehicleViewModel) -> Unit
