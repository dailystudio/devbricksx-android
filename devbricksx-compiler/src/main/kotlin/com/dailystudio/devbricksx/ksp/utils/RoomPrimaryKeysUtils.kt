package com.dailystudio.devbricksx.ksp.utils

import com.dailystudio.devbricksx.annotations.data.RoomCompanion
import com.dailystudio.devbricksx.ksp.helper.underlineCaseName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ksp.toTypeName

object RoomPrimaryKeysUtils {

    fun findPrimaryKeyNames(symbol: KSClassDeclaration,
                            resolver: Resolver): Set<String> {
        return findPrimaryKeys(symbol, resolver).keys
    }

    fun findPrimaryKeys(symbol: KSClassDeclaration,
                        resolver: Resolver): Map<String, KSType> {
        val allPropertiesInSymbol = symbol.getAllProperties()

        val roomCompanion = symbol.getAnnotation(RoomCompanion::class, resolver)
        val namesOfPrimaryKeys: MutableList<String> =
            roomCompanion?.findArgument("primaryKeys") ?: mutableListOf()

        if (namesOfPrimaryKeys.isEmpty()) {
            val nameOfAllProps = allPropertiesInSymbol.map {
                it.simpleName.getShortName()
            }

            val nameOfPropsInConstructor = mutableListOf<String>()
            symbol.primaryConstructor?.parameters?.forEach { param ->
                val nameOfParam = param.name?.getShortName() ?: return@forEach
                nameOfPropsInConstructor.add(nameOfParam)
            }

            val defaultPrimaryKey = if (nameOfPropsInConstructor.isNotEmpty()) {
                nameOfPropsInConstructor.first()
            } else {
                nameOfAllProps.first()
            }

            namesOfPrimaryKeys.add(defaultPrimaryKey)
        }

        val mapOfPrimaryKeys = mutableMapOf<String, KSType>()

        allPropertiesInSymbol.forEach {
            val nameOfProp = it.simpleName.getShortName()
            if (namesOfPrimaryKeys.contains(nameOfProp)) {
                mapOfPrimaryKeys[nameOfProp] = it.type.resolve()
            }
        }

        return mapOfPrimaryKeys
    }

    fun attachPrimaryKeysToMethodParameters(methodBuilder: FunSpec.Builder,
                                            primaryKeys: Map<String, KSType>) {
        primaryKeys.forEach {
            methodBuilder.addParameter(it.key, it.value.toTypeName())
        }
    }

    fun primaryKeysToSQLiteWhereClause(primaryKeys: Map<String, KSType>): String {
        return primaryKeyNamesToSQLiteWhereClause(primaryKeys.keys)
    }

    fun primaryKeyNamesToSQLiteWhereClause(namesOfPrimaryKeys: Set<String>): String {
        return buildString {
            append("where ")
            for ((i, nameOfPrimaryKey) in namesOfPrimaryKeys.withIndex()) {
                append(nameOfPrimaryKey.underlineCaseName())
                append(" = :")
                append(nameOfPrimaryKey)

                if (i < namesOfPrimaryKeys.size - 1) {
                    append(" and ")
                }
            }
        }
    }

    fun primaryKeysToFuncCallParameters(primaryKeys: Map<String, KSType>): String {
        return primaryKeyNamesToFuncCallParameters(primaryKeys.keys)
    }

    fun primaryKeyNamesToFuncCallParameters(namesOfPrimaryKeys: Set<String>): String {
        return namesOfPrimaryKeys.joinToString(separator = ", ")
    }

}