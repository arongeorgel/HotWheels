package com.garon.mvi.utils

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.subjects.Subject

fun <T> Any.unsafeListCast(): List<T> {
    @Suppress("UNCHECKED_CAST")
    return this as List<T>
}

fun <T> Subject<T>.toDisposableObserver(): DisposableObserver<T> =
    DisposableSubject(this)

operator fun CompositeDisposable.plusAssign(subscribe: Disposable) {
    add(subscribe)
}