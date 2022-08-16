package com.dailystudio.devbricksx.ksp.processors.step

import androidx.room.Entity
import com.dailylstudio.devbricksx.annotations.plus.RoomCompanion
import com.dailystudio.devbricksx.ksp.GeneratedResult
import com.dailystudio.devbricksx.ksp.ProcessStep
import com.dailystudio.devbricksx.ksp.helper.GeneratedNames
import com.dailystudio.devbricksx.ksp.processors.BaseSymbolProcessor
import com.dailystudio.devbricksx.ksp.utils.*
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.TypeSpec

class RoomCompanionStep (processor: BaseSymbolProcessor)
    : ProcessStep(RoomCompanion::class.qualifiedName!!, processor) {

    override fun process(resolver: Resolver, clazz: KSClassDeclaration): GeneratedResult? {
        val typeName = clazz.typeName()
        val packageName = clazz.packageName()
        val superClazz = clazz.superClassType()
        warn("clazz: [pack: $packageName, type: $typeName], super: $superClazz")

        val generateClassName = GeneratedNames.getRoomCompanionName(typeName)

        val typeOfAny = typeOfKotlinAny(resolver)
        if (superClazz != typeOfAny) {
            val companionOfSuperClazz = superClazz.getAnnotation(RoomCompanion::class, resolver)
            val superTypePackage = superClazz.packageName()
            val superTypeName = superClazz.typeName()

            warn("super roomCompanion: $companionOfSuperClazz")
        }

        val companion = clazz.getAnnotation(RoomCompanion::class, resolver)
        warn("roomCompanion: ${companion?.arguments}")

        val primaryKeys: List<String>? = companion?.findArgument("primaryKeys")
        warn("primaryKeys: $primaryKeys")

        if (primaryKeys.isNullOrEmpty()) {
            warn("primary keys are not specified for $clazz")
            return null
        }

        val primaryKeysString = buildPrimaryKeysString(primaryKeys)

        val entityAnnotationBuilder: AnnotationSpec.Builder =
            AnnotationSpec.builder(Entity::class)
                .addMember("tableName = %S",
                    GeneratedNames.getTableName(typeName)
                )

        val classBuilder = TypeSpec.classBuilder(generateClassName)
            .addAnnotation(entityAnnotationBuilder.build())


        return GeneratedResult(packageName, classBuilder)
    }


    private fun buildPrimaryKeysString(primaryKeys: List<String>): String {
        val prKeysBuilder = StringBuilder("{ ")
        for (i in primaryKeys.indices) {
            prKeysBuilder.append("\"")
            prKeysBuilder.append(primaryKeys[i])
            prKeysBuilder.append("\"")
            if (i < primaryKeys.size - 1) {
                prKeysBuilder.append(", ")
            }
        }
        prKeysBuilder.append(" }")
        return prKeysBuilder.toString()
    }

}