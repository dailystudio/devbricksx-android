package com.dailystudio.devbricksx.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.icu.util.Measure
import android.view.View

object AnimationUtils {

    private const val DEFAULT_ANIMATION_DURATION = 300L

    fun animateViewToHide(view: View,
                          duration: Long = DEFAULT_ANIMATION_DURATION) {
        if (view.visibility != View.VISIBLE) {
            return
        }

        view.animate()
                .translationX(-view.width.toFloat())
                .setDuration(duration)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        view.visibility = View.GONE
                    }
                })
    }

    fun animateViewToShow(view: View,
                          duration: Long = DEFAULT_ANIMATION_DURATION) {
        if (view.visibility == View.VISIBLE) {
            return
        }

        val widthSpec = View.MeasureSpec.makeMeasureSpec(0 ,View.MeasureSpec.UNSPECIFIED)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(0 ,View.MeasureSpec.UNSPECIFIED)
        view.measure(widthSpec, heightSpec)

        view.translationX = -view.measuredWidth.toFloat()
        view.visibility = View.VISIBLE

        view.animate()
                .translationX(0f)
                .setDuration(duration)
                .setListener(null)
    }

}