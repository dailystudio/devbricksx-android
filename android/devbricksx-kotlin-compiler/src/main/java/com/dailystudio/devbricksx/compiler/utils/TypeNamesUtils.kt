package com.dailystudio.devbricksx.compiler.utils

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName

class TypeNamesUtils {

    companion object {

        fun getViewModelScopeMemberName() : MemberName {
            return MemberName("androidx.lifecycle", "viewModelScope")
        }

        fun getLaunchMemberName() : MemberName {
            return MemberName("kotlinx.coroutines", "launch")
        }

        fun getJobTypeName() : ClassName {
            return ClassName.bestGuess("kotlinx.coroutines.Job")
        }

        fun getDispatchersTypeName() : ClassName {
            return ClassName.bestGuess("kotlinx.coroutines.Dispatchers")
        }

        fun getAndroidViewModelTypeName(): ClassName {
            return ClassName.bestGuess("androidx.lifecycle.AndroidViewModel")
        }

        fun getApplicationTypeName(): ClassName {
            return ClassName.bestGuess("android.app.Application")
        }

        fun getListTypeName(): ClassName {
            return ClassName("kotlin.collections", "List")
        }

        fun getPagedListTypeName(): ClassName {
            return ClassName("androidx.paging", "PagedList")
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

        fun getLiveDataOfListOfObjectTypeName(typeName: TypeName): TypeName {
            return getLiveDataOfTypeName(getListOfTypeName(typeName))
        }

        fun getPageListOfTypeName(typeClassName: TypeName): TypeName {
            val pagedList = getPagedListTypeName()

            return pagedList.parameterizedBy(typeClassName)
        }

        fun getLiveDataOfPagedListOfObjectsTypeName(typeName: TypeName): TypeName {
            return getLiveDataOfTypeName(getPageListOfTypeName(typeName))
        }

    }
}