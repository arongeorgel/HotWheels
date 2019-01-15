package com.garon.mvi.common

import com.garon.mvi.utils.plusAssign
import com.garon.mvi.utils.toDisposableObserver
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

abstract class BasePresenter<Intent, Action, Result, State> constructor(
    private val interactor: BaseInteractor<Action, Result>
) {

    private val intentsSubject = PublishSubject.create<Intent>()
    private val statesSubject = BehaviorSubject.create<State>()
    private val disposable = CompositeDisposable()

    internal abstract val defaultState: State
    internal abstract val lastIntention: Intent
    internal abstract val stateReducer: BiFunction<State, Result, State>
    internal abstract fun actionFromIntent(intent: Intent): Action

    init {
        disposable += compose().subscribeWith(statesSubject.toDisposableObserver())
    }

    fun processIntents(intents: Observable<Intent>): Disposable {
        return intents.subscribeWith(intentsSubject.toDisposableObserver())
    }

    fun states(): Observable<State> = statesSubject

    fun destroy() {
        disposable.dispose()
    }

    private fun compose(): Observable<State> {
        return intentsSubject
            .scan(initialIntentionFilter())
            .map { actionFromIntent(it) }
            .compose(interactor.actionsProcessor())
            .scan(defaultState, stateReducer)
    }

    private fun initialIntentionFilter() = { _: Intent, newIntention: Intent ->
        when (newIntention) {
            is InitialIntention -> lastIntention
            else -> newIntention
        }
    }
}

interface InitialIntention
