package com.garon.mvi.common

import io.reactivex.ObservableTransformer

abstract class BaseInteractor<Action, Result> {

    abstract fun actionsProcessor(): ObservableTransformer<Action, Result>

}
