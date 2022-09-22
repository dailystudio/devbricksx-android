package com.dailystudio.devbricksx.annotations.data

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class DataStoreCompanion(
    val storeType: StoreType = StoreType.SharedPreference
)