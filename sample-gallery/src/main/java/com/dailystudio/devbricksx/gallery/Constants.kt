package com.dailystudio.devbricksx.gallery

import com.nostra13.universalimageloader.core.DisplayImageOptions

object Constants {

    val DEFAULT_IMAGE_LOADER_OPTIONS_BUILDER: DisplayImageOptions.Builder = DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .cacheOnDisk(true)
        .showImageOnLoading(R.color.transparent)
        .showImageOnFail(R.color.transparent)
        .showImageForEmptyUri(R.color.transparent)
        .resetViewBeforeLoading(true)

    val DEFAULT_IMAGE_LOADER_OPTIONS: DisplayImageOptions =
            DEFAULT_IMAGE_LOADER_OPTIONS_BUILDER.build()

    const val EXTRA_TITLE = "com.dailystudio.devbricksx.samples.EXTRA_TITLE"

}