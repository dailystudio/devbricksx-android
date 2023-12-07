package com.dailystudio.devbricksx.annotations.viewmodel

import kotlin.reflect.KClass

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class ViewModel(val repository: KClass<*> = Unit::class,
                           val `group`: String = "")