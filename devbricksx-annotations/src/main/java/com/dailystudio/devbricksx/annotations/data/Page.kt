package com.dailystudio.devbricksx.annotations.data

import com.dailystudio.devbricksx.annotations.InternalConstants

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class Page(val pageSize: Int = DEFAULT_PAGE_SIZE) {

    companion object {
        const val DEFAULT_PAGE_SIZE = InternalConstants.DEFAULT_PAGE_SIZE
    }

}