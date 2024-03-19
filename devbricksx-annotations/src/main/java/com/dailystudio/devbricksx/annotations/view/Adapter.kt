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

@Repeatable
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class Adapter(
    val name: String = "",
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

@Repeatable
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class FragmentAdapter(
        val name: String = "",
        val pageFragment: KClass<*>,
        val diffUtil: KClass<*> = Unit::class,
)