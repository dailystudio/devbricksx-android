package com.dailystudio.devbricksx.ksp.utils

import com.dailystudio.devbricksx.ksp.helper.GeneratedNames
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.*

object TypeNameUtils {

    fun typeOfKotlinAny(resolver: Resolver): KSClassDeclaration? {
        return resolver.getClassDeclarationByName<Any>()
    }

    fun typeOf(resolver: Resolver, typeString: String): KSClassDeclaration? {
        return resolver.getClassDeclarationByName(typeString)
    }

    fun typeOfObject(packageName: String, typeName: String): ClassName {
        return ClassName(packageName, typeName)
    }

    fun typeOfCompanion(packageName: String, typeName: String): ClassName {
        return ClassName(packageName, GeneratedNames.getRoomCompanionName(typeName))
    }

    fun typeOfRoomDatabase(): ClassName {
        return ClassName("androidx.room", "RoomDatabase")
    }

    fun defaultValOfTypeName(typeName: TypeName): String {
        return if (typeName.isNullable) {
            return "null"
        } else {
            when (typeName) {
                STRING -> "\"\""
                INT, SHORT -> "0"
                LONG -> "0L"
                FLOAT -> "0f"
                DOUBLE -> "0.0"
                else -> {
                    ""
                }
            }
        }
    }

}


