package com.dailystudio.devbricksx.annotations

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class ListFragment(
        val layout: Int = -1,
        val layoutByName: String = "",
        val gridLayout: Boolean = false,
        val columns: Int = 2,
        val fillParent: Boolean = true,
        val superClass: KClass<*> = Void::class,
        val dataInterface: String = ""
)