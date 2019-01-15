package com.garon.mvi

import com.garon.mvi.navigation.VehicleListNavigator
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import retrofit2.Response

/**
 * Sample testing for MVI - the beauty of it is that we can pass an intention and it will go trough the presenter,
 * interactor, usecases, navigations, etc in order to have a beautiful integration test.
 *
 * Note: This test example does NOT exempt utils classes, or classes which have a logic which can be tested separately
 * with unit tests.
 */
class VehicleListTest {

    @Rule
    @JvmField
    val mockitoRule = MockitoJUnit.rule()

    @Mock
    lateinit var vehicleApi: VehicleListApi

    @Mock
    lateinit var navigator: VehicleListNavigator

    private lateinit var getVehicleListUseCase: GetVehicleListUseCase
    private lateinit var vehiclesPresenter: VehicleListPresenter
    private lateinit var vehiclesInteractor: VehicleListInteractor

    @Before
    fun setup() {
        getVehicleListUseCase = GetVehicleListUseCase(vehicleApi)
        vehiclesInteractor = VehicleListInteractor(getVehicleListUseCase, navigator, Schedulers.trampoline())
        vehiclesPresenter = VehicleListPresenter(vehiclesInteractor)
    }

    @Test
    fun `when initial intent trigger, retrieve successfully the vehicle list`() {
        whenever(vehicleApi.getVehicleList()).thenReturn(Observable.just(Response.success(successResponse1)))

        val subscriber = vehiclesPresenter.states().test()
        vehiclesPresenter.processIntents(Observable.just(VehiclesIntent.InitialIntent))

        subscriber.run {
            assertNoErrors()
            assertComplete()
            assertValueCount(3)
            // note - first value will always be the default state
            assertValueAt(1) { it.isLoading && it.list.isEmpty() && !it.isError }
            assertValueAt(2) { !it.isLoading && it.list.isNotEmpty() && !it.isError }
        }
    }

    @Test
    fun `when go to details intent trigger, check navigator`() {
        whenever(vehicleApi.getVehicleList()).thenReturn(Observable.just(Response.success(successResponse1)))

        val vehicle = VehicleViewModel(
            registrationCountry = "",
            registrationNumber = "",
            color = VehicleViewModel.Color.GREEN,
            type = VehicleViewModel.Type.CAR
        )

        val subscriber = vehiclesPresenter.states().test()
        vehiclesPresenter.processIntents(
            Observable.merge(
                Observable.just(VehiclesIntent.InitialIntent),
                Observable.just(VehiclesIntent.ViewVehicleIntent(vehicle))
            )
        )

        subscriber.run {
            assertNoErrors()
            assertComplete()
            assertValueCount(3)
            // quick redundant test.
            assertValueAt(2) { it.list.isNotEmpty() }
        }

        verify(navigator).goToVehicleDetails(vehicle)
    }

    @Test
    fun `when initial intent and load next intent trigger, retrieve successfully the vehicle list`() {
        whenever(vehicleApi.getVehicleList()).thenReturn(
            Observable.just(Response.success(successResponse1)),
            Observable.just(Response.success(successResponse2))
        )

        val subscriber = vehiclesPresenter.states().test()
        vehiclesPresenter.processIntents(
            Observable.merge(
                Observable.just(VehiclesIntent.InitialIntent),
                Observable.just(VehiclesIntent.LoadNextIntent)
            )
        )

        subscriber.run {
            assertNoErrors()
            assertComplete()
            assertValueCount(5)
            assertValueAt(1) { it.isLoading && !it.isError && it.list.isEmpty() }
            assertValueAt(2) { it.list.isNotEmpty() && !it.isLoading && !it.isError }
            assertValueAt(3) { it.isLoading && it.list.isNotEmpty() && !it.isError }
            assertValueAt(4) { it.list.isNotEmpty() && !it.isLoading && !it.isError }

            val firstPage = values()[2]
            val secondPage = values()[4]
            assert(firstPage.list[0].registrationNumber != secondPage.list[0].registrationNumber)
        }
    }

    @Test
    fun `when initial intent trigger, simulate api fail`() {
        whenever(vehicleApi.getVehicleList()).thenReturn(
            Observable.just(
                Response.error(
                    500, ResponseBody.create(MediaType.parse("APPLICATION_JSON"), "{}")
                )
            )
        )

        val subscriber = vehiclesPresenter.states().test()
        vehiclesPresenter.processIntents(Observable.just(VehiclesIntent.InitialIntent))

        subscriber.run {
            assertNoErrors()
            assertComplete()
            assertValueCount(3)
            subscriber.assertValueAt(1) { it.list.isEmpty() && !it.isError && it.isLoading }
            subscriber.assertValueAt(2) { it.list.isEmpty() && it.isError && !it.isLoading }
        }
    }
}
