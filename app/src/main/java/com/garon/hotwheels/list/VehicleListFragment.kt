package com.garon.hotwheels.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.garon.hotwheels.HotWheelApplication
import com.garon.hotwheels.R
import com.garon.hotwheels.kotterknife.bindView
import com.garon.mvi.VehicleListPresenter
import com.garon.mvi.VehicleViewModel
import com.garon.mvi.VehiclesIntent
import com.garon.mvi.VehiclesState
import com.garon.mvi.utils.plusAssign
import com.jakewharton.rxbinding3.swiperefreshlayout.refreshes
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class VehicleListFragment : Fragment() {

    companion object {
        fun create() = VehicleListFragment()
    }

    private val refreshLayout: SwipeRefreshLayout by bindView(R.id.vehicle_list_refresh)
    private val vehicleList: RecyclerView by bindView(R.id.vehicle_list)
    private val errorIcon: View by bindView(R.id.vehicle_list_error_icon)
    private val errorMessage: TextView by bindView(R.id.vehicle_list_error)

    private val disposable = CompositeDisposable()
    private val clickPublisher = PublishSubject.create<VehicleViewModel>()
    private val adapter = VehicleListAdapter { clickPublisher.onNext(it) }

    @Inject
    lateinit var presenter: VehicleListPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.vehicle_list_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inject()

        disposable += presenter
            .states()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::render)
        disposable += presenter.processIntents(intentions())

        val layoutManager = LinearLayoutManager(context)
        vehicleList.adapter = adapter
        vehicleList.layoutManager = layoutManager
        val loadListener = EndlessScrollListener(layoutManager, {})
    }

    private fun intentions(): Observable<VehiclesIntent> {
        return Observable.merge(
            Observable.just(VehiclesIntent.InitialIntent),
            refreshLayout.refreshes()
                .map { VehiclesIntent.RefreshIntent },
            clickPublisher.map {
                VehiclesIntent.ViewVehicleIntent(it)
            }
        )
    }

    private fun inject(): Unit? =
        activity?.let {
            (it.application as HotWheelApplication).appComponent
                .componentBuilder()
                .activity(it)
                .build()
                .inject(this)
        }

    override fun onDestroyView() {
        super.onDestroyView()

        disposable.dispose()
        presenter.destroy()
    }

    private fun render(state: VehiclesState) {
        refreshLayout.isRefreshing = state.isLoading


        if (state.isError) {
            vehicleList.visibility = View.GONE
            errorIcon.visibility = View.VISIBLE
            errorMessage.visibility = View.VISIBLE

            errorMessage.text = getString(R.string.generic_error_message)
        } else {
            vehicleList.visibility = View.VISIBLE
            errorIcon.visibility = View.GONE
            errorMessage.visibility = View.GONE

            adapter.updateData(state.list, state.isRefreshed)
        }
    }
}
