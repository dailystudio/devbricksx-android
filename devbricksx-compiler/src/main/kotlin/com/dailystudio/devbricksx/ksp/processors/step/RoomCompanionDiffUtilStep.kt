package com.dailystudio.devbricksx.ksp.processors.step

import com.dailystudio.devbricksx.annotations.data.RoomCompanion
import com.dailystudio.devbricksx.ksp.helper.GeneratedNames
import com.dailystudio.devbricksx.ksp.processors.BaseSymbolProcessor
import com.dailystudio.devbricksx.ksp.utils.*
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec

class RoomCompanionDiffUtilStep(processor: BaseSymbolProcessor)
    : AbsDiffUtilStep(RoomCompanion::class, processor) {

    override fun attachEqualsStatements(
        resolver: Resolver,
        symbol: KSClassDeclaration,
        methodItemsSameBuilder: FunSpec.Builder,
        methodContentsSameBuilder: FunSpec.Builder
    ) {
        val superSymbol = symbol.superClassType()

        val propsAll = symbol.getAllProperties().map { it.simpleName.getShortName() }.toSet()
        val propsInSuperType = superSymbol.getAllProperties().map { it.simpleName.getShortName() }.toSet()
        val typeOfAny = TypeNameUtils.typeOfKotlinAny(resolver)
        val hasSuperType = (superSymbol != typeOfAny)
        var supperTypeHasRoomCompanion = false
        var typeOfSuperDiffUtil: ClassName? = null

        if (hasSuperType) {
            val companionOfSuperType =
                superSymbol.getKSAnnotation(RoomCompanion::class, resolver)
            warn("super roomCompanion: $companionOfSuperType")
            supperTypeHasRoomCompanion = (companionOfSuperType != null)

            val packageNameOfSuperType = superSymbol.packageName()
            val typeNameOfSuperType = superSymbol.typeName()

            if (supperTypeHasRoomCompanion) {
                typeOfSuperDiffUtil = ClassName(
                    packageNameOfSuperType,
                    GeneratedNames.getDiffUtilName(typeNameOfSuperType)
                )
            }
        }

        val nameOfPrimaryKeys = RoomCompanionUtils.findPrimaryKeyNames(symbol)
        val equalsStatementOfPrimaryKeys = fieldsToEquals(nameOfPrimaryKeys)
        val equalsStatementOfFields = fieldsToEquals(propsAll - propsInSuperType)

        if (typeOfSuperDiffUtil != null) {
            methodItemsSameBuilder.addStatement(
                "return %T().areItemsTheSame(oldObject, newObject) && %L",
                typeOfSuperDiffUtil,
                equalsStatementOfPrimaryKeys)

            methodContentsSameBuilder.addStatement(
                "return %T().areContentsTheSame(oldObject, newObject) && %L",
                typeOfSuperDiffUtil,
                equalsStatementOfFields)
        } else {
            methodItemsSameBuilder.addStatement("return %L", equalsStatementOfPrimaryKeys)
            methodContentsSameBuilder.addStatement("return %L", equalsStatementOfFields)
        }
    }


    private fun fieldsToEquals(fields: Set<String>,
                               nameOfFirstObject: String = "oldObject",
                               nameOfSecondObject: String = "newObject"
    ): String {
        return buildString {
            for ((i, field) in fields.withIndex()) {
                append("(")
                append(nameOfFirstObject)
                append('.')
                append(field)
                append(" == ")
                append(nameOfSecondObject)
                append('.')
                append(field)
                append(")")

                if (i < fields.size - 1) {
                    append("\n && ")
                }
            }
        }
    }

}