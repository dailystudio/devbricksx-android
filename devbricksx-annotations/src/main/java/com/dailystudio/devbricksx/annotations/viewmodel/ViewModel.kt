package com.dailystudio.devbricksx.annotations.viewmodel

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class ViewModel(val repository: KClass<*> = Unit::class,
                           val `group`: String = "")