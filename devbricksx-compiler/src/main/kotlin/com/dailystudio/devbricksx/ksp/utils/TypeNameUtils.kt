package com.dailystudio.devbricksx.ksp.utils

import com.dailystudio.devbricksx.ksp.helper.GeneratedNames
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

object TypeNameUtils {

    fun typeOfKotlinAny(resolver: Resolver): KSClassDeclaration? {
        return resolver.getClassDeclarationByName<Any>()
    }

    fun typeOf(resolver: Resolver, typeString: String): KSClassDeclaration? {
        return resolver.getClassDeclarationByName(typeString)
    }

    fun typeOfContext(): ClassName {
        return ClassName("android.content", "Context")
    }

    fun typeOfObject(packageName: String, typeName: String): ClassName {
        return ClassName(packageName, typeName)
    }

    fun typeOfCompanion(objectType: ClassName): ClassName {
        return typeOfCompanion(objectType.packageName, objectType.simpleName)
    }

    fun typeOfCompanion(packageName: String, typeName: String): ClassName {
        return ClassName(packageName, GeneratedNames.getRoomCompanionName(typeName))
    }

    fun typeOfRoomDatabase(): ClassName {
        return ClassName("androidx.room", "RoomDatabase")
    }

    fun typeOfRoom(): ClassName {
        return ClassName("androidx.room", "Room")
    }

    fun typeOfRoomDatabaseBuilderOf(typeName: TypeName): TypeName {
        val builder = ClassName("androidx.room.RoomDatabase", "Builder")

        return builder.parameterizedBy(typeName)
    }

    fun typeOfMigration(): ClassName {
        return ClassName("androidx.room.migration", "Migration")
    }

    fun typeOfDummyMigration(): ClassName {
        return ClassName("com.dailystudio.devbricksx.database", "DummyMigration")
    }

    fun typeOfMapFunction(typeName1: TypeName, typeName2: TypeName): TypeName {
        val classNameOfMapFunction =
            ClassName("androidx.arch.core.util", "Function")
        return classNameOfMapFunction.parameterizedBy(
            typeName1, typeName2)
    }

    fun typeOfLiveDataMapFunction(): ClassName {
        return ClassName("androidx.lifecycle", "map")
    }

    fun typeOfFlowMapFunction(): TypeName {
        return ClassName("kotlinx.coroutines.flow", "map")
    }

    fun typeOfDataSourceFactory(): ClassName {
        return ClassName("androidx.paging.DataSource", "Factory")
    }

    fun typeOfDataSourceFactoryOf(typeName: TypeName): TypeName {
        val classNameODataSourceFactory = typeOfDataSourceFactory()

        return classNameODataSourceFactory.parameterizedBy(
            Integer::class.asClassName(), typeName)
    }

    fun typeOfPagingSource(): ClassName {
        return ClassName("androidx.paging", "PagingSource")
    }

    fun typeOfPagingSourceOf(typeName: TypeName): TypeName {
        val classNameOfPagingSource = typeOfPagingSource()

        return classNameOfPagingSource.parameterizedBy(
            INT, typeName)
    }

    fun typeOfLiveData(): ClassName {
        return ClassName("androidx.lifecycle", "LiveData")
    }

    fun typeOfLiveDataOf(typeName: TypeName): TypeName {
        val classNameOfLiveData = typeOfLiveData()

        return classNameOfLiveData.parameterizedBy(typeName)
    }

    fun typeOfPagedListBuilder(): ClassName {
        return ClassName("androidx.paging", "LivePagedListBuilder")
    }

    fun typeOfPagedList(): ClassName {
        return ClassName("androidx.paging", "PagedList")
    }

    fun typeOfPagedListOf(typeName: TypeName): TypeName {
        val classNameOfPagedList = typeOfPagedList()

        return classNameOfPagedList.parameterizedBy(typeName)
    }

    fun typeOfTransformations(): ClassName {
        return ClassName("androidx.lifecycle", "Transformations")
    }

    fun typeOfFlow(): ClassName {
        return ClassName("kotlinx.coroutines.flow", "Flow")
    }

    fun typeOfFlowOf(typeName: TypeName): TypeName {
        val typeOfFlow = typeOfFlow()

        return typeOfFlow.parameterizedBy(typeName)
    }

    fun typeOfList(): ClassName {
        return ClassName("kotlin.collections", "List")
    }

    fun typeOfListOf(typeName: TypeName): TypeName {
        val list = typeOfList()

        return list.parameterizedBy(typeName)
    }

    fun typeOfArrayOf(typeName: TypeName): TypeName {
        return ARRAY.parameterizedBy(typeName)
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

