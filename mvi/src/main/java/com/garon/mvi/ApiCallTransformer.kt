package com.garon.mvi

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import okhttp3.Headers
import retrofit2.Response

class ApiCallTransformer<T>(
    private val geekErrorMessage: String
) : ObservableTransformer<Response<T>, ApiCallStateResult> {

    private companion object {
        const val HTTP_BODY_CODE = 200
    }

    override fun apply(upstream: Observable<Response<T>>): ObservableSource<ApiCallStateResult> = upstream
        .map { response ->
            when {
                response.isSuccessful -> {
                    if (response.code() == HTTP_BODY_CODE && response.body() != null) {
                        ApiSuccessState(response.body(), response.headers())
                    } else {
                        ApiSuccessState(Unit, response.headers())
                    }
                }
                else -> ApiErrorState(geekErrorMessage, response.code())
            }
        }
}

interface ApiCallStateResult

data class ApiErrorState(
    val geekErrorMessage: String,
    val httpError: Int
) : ApiCallStateResult

data class ApiSuccessState<T>(
    val content: T,
    val headers: Headers
) : ApiCallStateResult
