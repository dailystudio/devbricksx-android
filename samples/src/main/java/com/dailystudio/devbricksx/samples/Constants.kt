package com.dailystudio.devbricksx.samples

import com.nostra13.universalimageloader.core.DisplayImageOptions

object Constants {

    val DEFAULT_IMAGE_LOADER_OPTIONS_BUILDER: DisplayImageOptions.Builder = DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .cacheOnDisk(true)
        .showImageOnLoading(com.dailystudio.devbricksx.R.color.transparent)
        .showImageOnFail(com.dailystudio.devbricksx.R.color.transparent)
        .showImageForEmptyUri(com.dailystudio.devbricksx.R.color.transparent)
        .resetViewBeforeLoading(true)

    val DEFAULT_IMAGE_LOADER_OPTIONS: DisplayImageOptions =
            DEFAULT_IMAGE_LOADER_OPTIONS_BUILDER.build()

    const val EXTRA_TITLE = "com.dailystudio.devbricksx.samples.EXTRA_TITLE"

}