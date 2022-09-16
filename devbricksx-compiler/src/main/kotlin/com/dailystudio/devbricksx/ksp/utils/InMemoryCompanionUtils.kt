package com.dailystudio.devbricksx.ksp.utils

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName

object InMemoryCompanionUtils {

    fun findInMemoryObjectInSuperTypes(symbol: KSClassDeclaration): KSType? {
        val typeOfInMemoryObject = TypeNameUtils.typeOfInMemoryObject()
        symbol.getAllSuperTypes().forEach {
            val classNameOfSupertype = it.toClassName()
            if (classNameOfSupertype == typeOfInMemoryObject) {
                return it
            }
        }

        return null
    }

    fun getKeyForInMemoryObject(symbol: KSClassDeclaration): TypeName? {
        val found = findInMemoryObjectInSuperTypes(symbol)

        return found?.arguments?.first()?.toTypeName()
    }

}