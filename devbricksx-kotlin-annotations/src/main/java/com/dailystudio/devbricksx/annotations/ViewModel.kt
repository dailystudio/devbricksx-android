package com.dailystudio.devbricksx.annotations

import kotlin.reflect.KClass

@Deprecated("ViewModel is deprecated by ViewModel in package com.dailystudio.devbricksx.annotations.viewmodel")
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class ViewModel(val repository: KClass<*> = Void::class,
                           val `group`: String = "")