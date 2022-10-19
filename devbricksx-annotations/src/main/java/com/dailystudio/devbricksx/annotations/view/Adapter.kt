package com.dailystudio.devbricksx.annotations.view

import kotlin.reflect.KClass

enum class ViewType {
    SingleLine,
    Card,
    CardInformative,
    CardImmersive,
    Page,
    Customized,
}

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class Adapter(
        val viewHolder: KClass<*>,
        val viewType: ViewType = ViewType.SingleLine,
        val layout: Int = -1,
        val layoutByName: String = "",
        val paged: Boolean = false,
        val notifyAfterListChanged: Boolean = false,
        val diffUtil: KClass<*> = Unit::class,
)

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class ExperimentalPagingDataAdapter

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class FragmentAdapter(
        val pageFragment: KClass<*>
)