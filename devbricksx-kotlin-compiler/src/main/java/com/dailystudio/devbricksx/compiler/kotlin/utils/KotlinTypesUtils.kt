package com.dailystudio.devbricksx.compiler.kotlin.utils

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName

object KotlinTypesUtils {

    fun javaToKotlinTypeName(objectTypeName: TypeName,
                             origTypeName: TypeName): TypeName {
        val array = TypeNamesUtils.getArrayTypeName(objectTypeName)
        val javaLong = TypeNamesUtils.getJavaLongTypeName()
        val javaInteger = TypeNamesUtils.getJavaIntegerTypeName()
        val javaString = TypeNamesUtils.getJavaStringTypeName()

        when (origTypeName) {
            is ParameterizedTypeName -> {
                val liveDataTypeName = TypeNamesUtils.getLiveDataTypeName()
                val javaListTypeName = TypeNamesUtils.getJavaListTypeName()
                val listTypeName = TypeNamesUtils.getListTypeName()
                val pagingSourceTypeName = TypeNamesUtils.getPagingSourceTypeName()
                val flowTypeName = TypeNamesUtils.getFlowTypeName()

                println("rawType = ${origTypeName.rawType}")
                println("typeArguments = ${origTypeName.typeArguments}")

                val rawType = origTypeName.rawType
                val typeArguments = origTypeName.typeArguments

                when (rawType) {
                    javaListTypeName -> {
                        if (typeArguments.isNotEmpty()) {
                            val newTypeName = javaToKotlinTypeName(objectTypeName,
                                    typeArguments[0])

                            return listTypeName.parameterizedBy(newTypeName)
                        }
                    }

                    liveDataTypeName -> {
                        if (typeArguments.isNotEmpty()) {
                            val newTypeName = javaToKotlinTypeName(objectTypeName,
                                    typeArguments[0])

                            return liveDataTypeName.parameterizedBy(newTypeName)
                        }
                    }

                    pagingSourceTypeName -> {
                        if (typeArguments.isNotEmpty()) {
                            val newTypeName = javaToKotlinTypeName(objectTypeName,
                                    typeArguments[0])

                            return pagingSourceTypeName.parameterizedBy(newTypeName, typeArguments[1])
                        }
                    }

                    flowTypeName -> {
                        if (typeArguments.isNotEmpty()) {
                            val newTypeName = javaToKotlinTypeName(objectTypeName,
                                    typeArguments[0])

                            return flowTypeName.parameterizedBy(newTypeName)
                        }
                    }

                    array -> {
                        if (typeArguments.isNotEmpty()) {
                            val ta0Name = typeArguments[0].toString()
                            when (ta0Name) {
                                "kotlin.Int" -> {
                                    val primitiveType = ta0Name.removePrefix("kotlin")
                                    return ClassName("kotlin", "${primitiveType}Array")
                                }
                            }
                        }
                    }
                }
            }

            javaLong -> {
                return TypeNamesUtils.getLongTypeName()
            }

            javaInteger -> {
                return TypeNamesUtils.getIntegerTypeName()
            }

            javaString -> {
                return TypeNamesUtils.getStringTypeName()
            }
        }

        return origTypeName
    }

}