package com.dailystudio.devbricksx.compose.animation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween


private const val NORMAL_ANIM_DURATION_MILLIS: Int = 400


fun <S> AnimatedContentTransitionScope<S>.slideInTransition(
    towards: AnimatedContentTransitionScope.SlideDirection,
    durationMillis: Int = NORMAL_ANIM_DURATION_MILLIS
): EnterTransition {
    return slideIntoContainer(
        towards = towards,
        animationSpec = tween(durationMillis)
    )
}

fun <S> AnimatedContentTransitionScope<S>.slideOutTransition(
    towards: AnimatedContentTransitionScope.SlideDirection,
    durationMillis: Int = NORMAL_ANIM_DURATION_MILLIS
): ExitTransition {
    return slideOutOfContainer(
        towards = towards,
        animationSpec = tween(durationMillis)
    )
}

fun <S> AnimatedContentTransitionScope<S>.rightInTransition(
    durationMillis: Int = NORMAL_ANIM_DURATION_MILLIS
): EnterTransition {
    return slideInTransition(
        towards = AnimatedContentTransitionScope.SlideDirection.Left,
        durationMillis
    )
}

fun <S> AnimatedContentTransitionScope<S>.leftInTransition(
    durationMillis: Int = NORMAL_ANIM_DURATION_MILLIS
): EnterTransition {
    return slideInTransition(
        towards = AnimatedContentTransitionScope.SlideDirection.Right,
        durationMillis
    )
}

fun <S> AnimatedContentTransitionScope<S>.leftOutTransition(
    durationMillis: Int = NORMAL_ANIM_DURATION_MILLIS
): ExitTransition {
    return slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(durationMillis)
    )
}

fun <S> AnimatedContentTransitionScope<S>.rightOutTransition(
    durationMillis: Int = NORMAL_ANIM_DURATION_MILLIS
): ExitTransition {
    return slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(durationMillis)
    )
}



