package com.dailystudio.devbricksx.annotations

import kotlin.reflect.KClass

@Deprecated("ViewType is deprecated by ViewType in package com.dailystudio.devbricksx.annotations.view")
enum class ViewType {
    SingleLine,
    Card,
    CardInformative,
    CardImmersive,
    Page,
    Customized,
}

@Deprecated("Adapter is deprecated by Adapter in package com.dailystudio.devbricksx.annotations.view")
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class Adapter(
        val viewHolder: KClass<*>,
        val viewType: ViewType = ViewType.SingleLine,
        val layout: Int = -1,
        val layoutByName: String = "",
        val paged: Boolean = false,
        val notifyAfterListChanged: Boolean = false
)

@Deprecated("ExperimentalPagingDataAdapter is deprecated by ExperimentalPagingDataAdapter in package com.dailystudio.devbricksx.annotations.view")
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class ExperimentalPagingDataAdapter

@Deprecated("FragmentAdapter is deprecated by FragmentAdapter in package com.dailystudio.devbricksx.annotations.view")
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class FragmentAdapter(
        val pageFragment: KClass<*>
)