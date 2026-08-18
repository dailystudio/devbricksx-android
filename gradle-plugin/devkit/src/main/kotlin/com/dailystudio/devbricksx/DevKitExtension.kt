package com.dailystudio.devbricksx

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

open class DevKitExtension @Inject constructor(objects: ObjectFactory) {
    val useAnnotations: Property<Boolean> = objects.property(Boolean::class.java).convention(true)
    val compileType: Property<String> = objects.property(String::class.java).convention("Library")
    val devKitComps: ListProperty<String> = objects.listProperty(String::class.java).convention(emptyList<String>())

    override fun toString(): String {
        return buildString {
            append("useAnnotations = ${useAnnotations.get()}")
            append("compileType = ${compileType.get()}")
            append("components = ${devKitComps.get().joinToString(",")}")
        }
    }

}