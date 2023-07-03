package com.dailystudio.devbricksx.gallery

import java.util.concurrent.TimeUnit

object Constants {

    const val QUERY_ALL = "*"

    val IMAGES_CACHE_TIMEOUT = TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES)
}