package com.dailystudio.devbricksx.ksp.processors.step

import androidx.room.Dao
import androidx.room.Query
import com.dailylstudio.devbricksx.annotations.plus.RoomCompanion
import com.dailystudio.devbricksx.ksp.GeneratedResult
import com.dailystudio.devbricksx.ksp.SingleSymbolProcessStep
import com.dailystudio.devbricksx.ksp.helper.GeneratedNames
import com.dailystudio.devbricksx.ksp.helper.toVariableOrParamName
import com.dailystudio.devbricksx.ksp.helper.toVariableOrParamNameOfCollection
import com.dailystudio.devbricksx.ksp.processors.BaseSymbolProcessor
import com.dailystudio.devbricksx.ksp.utils.RoomPrimaryKeysUtils
import com.dailystudio.devbricksx.ksp.utils.TypeNamesUtils
import com.dailystudio.devbricksx.ksp.utils.packageName
import com.dailystudio.devbricksx.ksp.utils.typeName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec

class DaoStep (processor: BaseSymbolProcessor)
    : SingleSymbolProcessStep(RoomCompanion::class.qualifiedName!!, processor) {

    companion object {
        private const val METHOD_GET_ONE = "_getOne"
    }

    override fun processSymbol(resolver: Resolver, symbol: KSClassDeclaration): GeneratedResult? {
        val typeName = symbol.typeName()
        val packageName = symbol.packageName()

        val typeNameToGenerate = GeneratedNames.getRoomCompanionDaoName(typeName)

        val primaryKeys = RoomPrimaryKeysUtils.findPrimaryKeys(symbol, resolver)

        val typeOfObject = TypeNamesUtils.typeOfObject(packageName, typeName)
        val typeOfCompanion = TypeNamesUtils.typeOfCompanion(packageName, typeName)
        val typeOfListOfObjects = TypeNamesUtils.typeOfListOf(typeOfObject)
        val typeOfListOfCompanions = TypeNamesUtils.typeOfListOf(typeOfCompanion)
        val nameOfObject = typeName.toVariableOrParamName()
        val nameOfCompanion = typeNameToGenerate.toVariableOrParamName()
        val nameOfCompanions = typeNameToGenerate.toVariableOrParamNameOfCollection()

        val tableName = GeneratedNames.getTableName(typeName)
        val whereClauseForGetOneMethods  =
            RoomPrimaryKeysUtils.primaryKeysToSQLiteWhereClause(primaryKeys)

        val classBuilder = TypeSpec.classBuilder(typeNameToGenerate)
            .addAnnotation(Dao::class)
            .addModifiers(KModifier.ABSTRACT)

        val methodGetOneBuilder: FunSpec.Builder =
            FunSpec.builder(METHOD_GET_ONE)
                .addModifiers(KModifier.PUBLIC, KModifier.ABSTRACT)
                .returns(typeOfCompanion)

        RoomPrimaryKeysUtils.attachPrimaryKeysToMethodParameters(methodGetOneBuilder, primaryKeys)
        methodGetOneBuilder.addAnnotation(
            AnnotationSpec.builder(Query::class)
                .addMember("value = %S", "SELECT * FROM `$tableName` $whereClauseForGetOneMethods")
                .build()
        )

        classBuilder.addFunction(methodGetOneBuilder.build())

        return GeneratedResult(packageName, classBuilder)
    }

}