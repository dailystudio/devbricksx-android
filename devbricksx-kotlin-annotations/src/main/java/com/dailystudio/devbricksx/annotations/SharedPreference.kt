package com.dailystudio.devbricksx.annotations

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class SharedPreference()

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FIELD)
annotation class PreferenceValue(val prefName: String = "",
                                 val defaultValueStr: String = "")