package com.dailystudio.devbricksx

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

open class DevKitExtension @Inject constructor(
    objects: ObjectFactory
) {
    var useAnnotations = objects.property<Boolean>().convention(false)
    var devKitComp: ListProperty<String> = objects.listProperty<String>().convention(
        listOf(
        "a", "b")
    )

    override fun toString(): String {
        return buildString {
            append("useAnnotations = ${useAnnotations.get()}")
            append(devKitComp.get().joinToString(","))
        }
    }

}