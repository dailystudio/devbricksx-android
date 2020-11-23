package com.dailystudio.devbricksx.utils

import android.content.Context
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.dailystudio.devbricksx.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

enum class ShowDirection {
    LEFT, TOP, RIGHT, BOTTOM
}

fun FloatingActionButton.showWithAnimation(context: Context,
                                           direction: ShowDirection) {
    return showWithAnimation(context, when (direction) {
        ShowDirection.TOP -> R.anim.fab_in_top
        ShowDirection.BOTTOM -> R.anim.fab_in_bottom
        ShowDirection.LEFT -> R.anim.fab_in_left
        ShowDirection.RIGHT -> R.anim.fab_in_right
    })
}

fun FloatingActionButton.showWithAnimation(context: Context,
                      animResId: Int) {
    val anim = AnimationUtils.loadAnimation(
            context, animResId)

    return showWithAnimation(anim)
}

fun  FloatingActionButton.showWithAnimation(animation: Animation) {
    startAnimation(animation)
}