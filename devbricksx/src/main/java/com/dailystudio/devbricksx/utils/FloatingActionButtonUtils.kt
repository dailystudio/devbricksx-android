package com.dailystudio.devbricksx.utils

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.dailystudio.devbricksx.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Enumeration for FloatingActionButton animation directions.
 */
enum class FabAnimationDirection {
    LEFT, TOP, RIGHT, BOTTOM
}

/**
 * Shows the FloatingActionButton with an animation.
 *
 * @param context The context.
 * @param direction The direction from which the FAB should appear.
 */
fun FloatingActionButton.showWithAnimation(context: Context,
                                           direction: FabAnimationDirection) {
    return showWithAnimation(context, when (direction) {
        FabAnimationDirection.TOP -> R.anim.fab_in_top
        FabAnimationDirection.BOTTOM -> R.anim.fab_in_bottom
        FabAnimationDirection.LEFT -> R.anim.fab_in_left
        FabAnimationDirection.RIGHT -> R.anim.fab_in_right
    })
}

/**
 * Shows the FloatingActionButton with a custom animation resource.
 *
 * @param context The context.
 * @param animResId The animation resource ID.
 */
fun FloatingActionButton.showWithAnimation(context: Context,
                      animResId: Int) {
    val anim = AnimationUtils.loadAnimation(
            context, animResId)

    return showWithAnimation(anim)
}

/**
 * Shows the FloatingActionButton with a custom animation object.
 *
 * @param animation The animation object.
 */
fun  FloatingActionButton.showWithAnimation(animation: Animation) {
    visibility = View.VISIBLE
    startAnimation(animation)
}


/**
 * Hides the FloatingActionButton with an animation.
 *
 * @param context The context.
 * @param direction The direction to which the FAB should disappear.
 */
fun FloatingActionButton.hideWithAnimation(context: Context,
                                           direction: FabAnimationDirection) {
    return hideWithAnimation(context, when (direction) {
        FabAnimationDirection.TOP -> R.anim.fab_out_top
        FabAnimationDirection.BOTTOM -> R.anim.fab_out_bottom
        FabAnimationDirection.LEFT -> R.anim.fab_out_left
        FabAnimationDirection.RIGHT -> R.anim.fab_out_right
    })
}


/**
 * Hides the FloatingActionButton with a custom animation resource.
 *
 * @param context The context.
 * @param animResId The animation resource ID.
 */
fun FloatingActionButton.hideWithAnimation(context: Context,
                      animResId: Int) {
    val anim = AnimationUtils.loadAnimation(
            context, animResId)

    return hideWithAnimation(anim)
}

/**
 * Hides the FloatingActionButton with a custom animation object.
 *
 * @param animation The animation object.
 */
fun  FloatingActionButton.hideWithAnimation(animation: Animation) {

    animation.setAnimationListener(object: Animation.AnimationListener {

        override fun onAnimationStart(animation: Animation?) {
        }

        override fun onAnimationEnd(animation: Animation?) {
            visibility = View.GONE
            animation?.setAnimationListener(null)
        }

        override fun onAnimationRepeat(animation: Animation?) {
        }

    })

    startAnimation(animation)
}