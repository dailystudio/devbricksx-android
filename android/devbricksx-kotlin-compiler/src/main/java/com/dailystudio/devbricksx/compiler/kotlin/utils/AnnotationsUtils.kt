package com.dailystudio.devbricksx.compiler.kotlin.utils

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asTypeName
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.SimpleAnnotationValueVisitor7

class AnnotationsUtils {

    companion object {

        fun getClassValueFromAnnotation(typeElement: TypeElement,
                                        property: String): ClassName? {
            val value: Any? = findPropertyInAnnotation(typeElement, property)

            if (value !is TypeMirror) {
                return null
            }

            return value.asTypeName() as ClassName
        }

        private fun findPropertyInAnnotation(typeElement: TypeElement,
                                             property: String): Any? {
            if (property.isEmpty()) {
                return null
            }

            val annotationMirrors = typeElement.annotationMirrors
            for (annotationMirror in annotationMirrors) {
                val elementValues = annotationMirror.elementValues
                for ((key1, value1) in elementValues) {
                    val key = key1!!.simpleName.toString()
                    val value = value1!!.value
                    if (key == property) {
                        return value
                    }
                }
            }

            return null
        }

    }

    class OwnValueVisitor : SimpleAnnotationValueVisitor7<Any?, Void?>() {

        fun visitAnnotation(a: AnnotationMirror, p: Void): Any {
            System.out.printf(">> annotationTypeValue: %s\n", a.toString())
            return a
        }
    }

}