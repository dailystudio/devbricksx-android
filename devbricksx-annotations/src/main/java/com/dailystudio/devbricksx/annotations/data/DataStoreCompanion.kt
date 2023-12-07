package com.dailystudio.devbricksx.annotations.data

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class DataStoreCompanion(
    val storeType: StoreType = StoreType.SharedPreference
)