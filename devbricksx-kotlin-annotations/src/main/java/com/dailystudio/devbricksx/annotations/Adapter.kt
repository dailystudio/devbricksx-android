package com.dailystudio.devbricksx.annotations

import kotlin.reflect.KClass

enum class ViewType {
    SingleLine,
    Card,
    CardInformative,
    Page,
    Customized,
}

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class Adapter(
        val viewHolder: KClass<*>,
        val viewType: ViewType = ViewType.SingleLine,
        val layout: Int = -1,
        val paged: Boolean = true
)