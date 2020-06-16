package com.dailystudio.devbricksx.compiler.kotlin.utils

import com.dailystudio.devbricksx.annotations.RoomCompanion
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.util.Types

object FieldsUtils {

    fun collectPrimaryKeyFields(element: Element,
                                objectTypeName: TypeName,
                                fields: MutableMap<String, TypeName>,
                                typeUtils: Types) {
        if (element !is TypeElement) {
            return
        }

        val companionAnnotation = element.getAnnotation(RoomCompanion::class.java) ?: return
        val primaryKeys: Array<String> = companionAnnotation.primaryKeys
        if (primaryKeys.isEmpty()) {
            return
        }

        val primaryKeySet: MutableSet<String> = mutableSetOf()
        for (key in primaryKeys) {
            primaryKeySet.add(key)
        }

        val subElements = element.enclosedElements
        var varElement: VariableElement
        for (subElement in subElements) {
            if (subElement is VariableElement) {
                varElement = subElement
                val varName = varElement.simpleName.toString()
                val fieldType = varElement.asType()
                if (primaryKeySet.contains(varName)) {
                    fields[varName] = KotlinTypesUtils.javaToKotlinTypeName(objectTypeName,
                            fieldType.asTypeName())
                }
            }
        }
        val superClass = element.superclass
        print("superClass: $superClass")
        if (superClass != null) {
            collectPrimaryKeyFields(typeUtils.asElement(superClass),
                    objectTypeName, fields, typeUtils)
        }
    }

    fun primaryKeyFieldsToMethodParameters(methodBuilder: FunSpec.Builder,
                                           primaryKeyFields: Map<String, TypeName>) {

        for ((filedName, fieldType) in primaryKeyFields) {
            methodBuilder.addParameter(filedName, fieldType)
        }
    }

    fun primaryKeyFieldsToFuncCallParameters(primaryKeyFields: Map<String, TypeName>): String {
        val keys = primaryKeyFields.keys.toTypedArray()

        val builder = StringBuilder()
        var filedName: String
        for (i in keys.indices) {
            filedName = keys[i]
            builder.append(filedName)
            if (i < keys.size - 1) {
                builder.append(",")
            }
        }

        return builder.toString()
    }

}