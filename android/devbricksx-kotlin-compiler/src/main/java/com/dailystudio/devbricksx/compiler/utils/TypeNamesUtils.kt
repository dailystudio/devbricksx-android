package com.dailystudio.devbricksx.compiler.utils

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import java.lang.reflect.Type

class TypeNamesUtils {

    companion object {

        fun getAndroidViewModelTypeName(): ClassName {
            return ClassName.bestGuess("androidx.lifecycle.AndroidViewModel")
        }

        fun getApplicationTypeName(): ClassName {
            return ClassName.bestGuess("android.app.Application")
        }

        fun getListTypeName(): ClassName {
            return ClassName("kotlin.collections", "List")
        }

        fun getArrayListTypeName(): ClassName {
            return ClassName("kotlin.collections", "ArrayList")
        }

        fun getLiveDataTypeName(): ClassName {
            return ClassName("androidx.lifecycle", "LiveData")
        }

        fun getListOfTypeName(typeName: TypeName): TypeName {
            val list = getListTypeName()

            return list.parameterizedBy(typeName)
        }

        fun getLiveDataOfTypeName(typeName: TypeName): TypeName {
            val liveData = getLiveDataTypeName()

            return liveData.parameterizedBy(typeName)
        }

        fun getLiveDataOfListOfObjectName(typeName: TypeName): TypeName {
            return getLiveDataOfTypeName(getListOfTypeName(typeName))
        }

    }
}