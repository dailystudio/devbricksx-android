package com.dailystudio.devbricksx.samples

import androidx.navigation.NavOptions
import com.dailystudio.devbricksx.R as devbricksxR

object NaviAnimations {

    val naviOptions =
        NavOptions.Builder()
            .setEnterAnim(devbricksxR.anim.slide_in_right)
            .setExitAnim(devbricksxR.anim.slide_out_left)
            .setPopEnterAnim(devbricksxR.anim.slide_in_left)
            .setPopExitAnim(devbricksxR.anim.slide_out_right)
            .build()
}