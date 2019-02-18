package com.garon.hotwheels

import android.animation.Animator
import android.animation.ObjectAnimator
import android.graphics.Rect
import android.transition.TransitionValues
import android.transition.Visibility
import android.view.View
import android.view.ViewGroup

class ExpandOut : Visibility() {

    companion object {
        private const val KEY_SCREEN_BOUNDS = "screenBounds"
    }

    private val tempLoc = IntArray(2)

    private fun captureValues(transitionValues: TransitionValues) {
        val view = transitionValues.view
        view.getLocationOnScreen(tempLoc)
        val left = tempLoc[0]
        val top = tempLoc[1]
        val right = left + view.width
        val bottom = top + view.height
        transitionValues.values[KEY_SCREEN_BOUNDS] = Rect(left, top, right, bottom)
    }

    override fun captureStartValues(transitionValues: TransitionValues) {
        super.captureStartValues(transitionValues)
        captureValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        super.captureEndValues(transitionValues)
        captureValues(transitionValues)
    }

    override fun onAppear(
        sceneRoot: ViewGroup, view: View,
        startValues: TransitionValues?, endValues: TransitionValues?
    ): Animator? {
        if (endValues == null) return null

        val bounds = endValues.values[KEY_SCREEN_BOUNDS] as Rect
        val endY = view.translationY
        val startY = endY + calculateDistance(sceneRoot, bounds)
        return ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, startY, endY)
    }

    override fun onDisappear(
        sceneRoot: ViewGroup, view: View,
        startValues: TransitionValues?, endValues: TransitionValues?
    ): Animator? {
        if (startValues == null) return null

        val bounds = startValues.values[KEY_SCREEN_BOUNDS] as Rect
        val startY = view.translationY
        val endY = startY + calculateDistance(sceneRoot, bounds)
        return ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, startY, endY)
    }

    private fun calculateDistance(sceneRoot: View, viewBounds: Rect): Int {
        sceneRoot.getLocationOnScreen(tempLoc)
        val sceneRootY = tempLoc[1]
        return when {
            epicenter == null -> -sceneRoot.height
            viewBounds.top <= epicenter.top -> sceneRootY - epicenter.top
            else -> sceneRootY + sceneRoot.height - epicenter.bottom
        }
    }
}