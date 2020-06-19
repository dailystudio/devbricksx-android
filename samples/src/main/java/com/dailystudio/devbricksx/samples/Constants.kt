package com.dailystudio.devbricksx.samples

import com.nostra13.universalimageloader.core.DisplayImageOptions

object Constants {

    val DEFAULT_IMAGE_LOADER_OPTIONS: DisplayImageOptions = DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .cacheOnDisk(true)
        .showImageOnLoading(R.color.transparent)
        .showImageOnFail(R.color.transparent)
        .showImageForEmptyUri(R.color.transparent)
        .resetViewBeforeLoading(true)
        .build()

    const val EXTRA_TITLE = "com.dailystudio.devbricksx.samples.EXTRA_TITLE"

}