package com.dailystudio.devbricksx.annotations

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class ViewModel(val repository: KClass<*> = Void::class,
                           val `group`: String = "",
                           val packageName: String = "")