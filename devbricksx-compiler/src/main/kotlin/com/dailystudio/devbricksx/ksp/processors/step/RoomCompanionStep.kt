package com.dailystudio.devbricksx.ksp.processors.step

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dailylstudio.devbricksx.annotations.plus.RoomCompanion
import com.dailystudio.devbricksx.ksp.GeneratedResult
import com.dailystudio.devbricksx.ksp.SingleSymbolProcessStep
import com.dailystudio.devbricksx.ksp.helper.GeneratedNames
import com.dailystudio.devbricksx.ksp.helper.underlineCaseName
import com.dailystudio.devbricksx.ksp.processors.BaseSymbolProcessor
import com.dailystudio.devbricksx.ksp.utils.*
import com.google.devtools.ksp.isConstructor
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toTypeName

class RoomCompanionStep (processor: BaseSymbolProcessor)
    : SingleSymbolProcessStep(RoomCompanion::class.qualifiedName!!, processor) {

    override fun processSymbol(resolver: Resolver, symbol: KSClassDeclaration): GeneratedResult? {
        val typeName = symbol.typeName()
        val packageName = symbol.packageName()
        val superClazz = symbol.superClassType()
        warn("clazz: [pack: $packageName, type: $typeName], super: $superClazz")

        val typeNameToGenerate = GeneratedNames.getRoomCompanionName(typeName)

        val typeOfAny = typeOfKotlinAny(resolver)
        if (superClazz != typeOfAny) {
            val companionOfSuperClazz = superClazz.getAnnotation(RoomCompanion::class, resolver)
            val superTypePackage = superClazz.packageName()
            val superTypeName = superClazz.typeName()

            warn("super roomCompanion: $companionOfSuperClazz")
        }

        val companion = symbol.getAnnotation(RoomCompanion::class, resolver)
        warn("roomCompanion: ${companion?.arguments}")

        val primaryKeys: MutableList<String> =
            companion?.findArgument("primaryKeys") ?: mutableListOf()
        warn("primaryKeys: $primaryKeys")

        val paramsInConstructor = mutableMapOf<String, KSValueParameter>()
        val constructorBuilder = FunSpec.constructorBuilder()
        for (func in symbol.getAllFunctions()) {
            if (func.isConstructor()) {
                for (param in func.parameters) {
                    warn("processing constructor params: $param")
                    val nameOfParam = param.name?.getShortName() ?: continue

                    val paramSpecBuilder = ParameterSpec.builder(nameOfParam,
                        param.type.toTypeName())

                    paramsInConstructor[nameOfParam] = param
                    constructorBuilder.addParameter(paramSpecBuilder.build())
                }
            }
        }

        val propSpecs = mutableListOf<PropertySpec>()
        val primaryKeysFound = mutableListOf<String>()
        for ((i, prop) in symbol.getAllProperties().withIndex()) {
            val nameOfProp = prop.simpleName.getShortName()
            val typeOfProp = prop.type.toTypeName()
            warn("processing prop [$i]: $prop [${typeOfProp}]")
            val propSpecBuilder = PropertySpec.builder(nameOfProp, typeOfProp)
                .mutable(true) // must to mutable here for Room processing

            if (paramsInConstructor.containsKey(nameOfProp)) {
                propSpecBuilder.initializer(nameOfProp)
            } else {
                val defaultVal = defaultValOfTypeName(typeOfProp)
                warn("default val of [$typeOfProp]: $defaultVal")
                propSpecBuilder.initializer(defaultValOfTypeName(prop.type.toTypeName()))
            }

            propSpecBuilder.addAnnotation(
                AnnotationSpec.builder(ColumnInfo::class)
                    .addMember("name = %S", nameOfProp.underlineCaseName())
                    .build()
            )

            if (primaryKeys.isEmpty() && i == 0) {
                primaryKeys.add(nameOfProp)
            }

            if (primaryKeys.contains(nameOfProp)) {
                propSpecBuilder.addAnnotation(PrimaryKey::class)
                primaryKeysFound.add(nameOfProp)
            }

            propSpecs.add(propSpecBuilder.build())
        }

        if (primaryKeysFound != primaryKeys) {
            error("Not all primary keys are found ${primaryKeys - primaryKeysFound} in $symbol")
            return null
        }

        val entityAnnotationBuilder: AnnotationSpec.Builder =
            AnnotationSpec.builder(Entity::class)
                .addMember("tableName = %S",
                    GeneratedNames.getTableName(typeName)
                )

        if (primaryKeys.size > 1) {
            entityAnnotationBuilder.addMember("primaryKeys", "\$L",
                buildPrimaryKeysString(primaryKeys))
        }

        val classBuilder = TypeSpec.classBuilder(typeNameToGenerate)
            .addAnnotation(entityAnnotationBuilder.build())
            .primaryConstructor(constructorBuilder.build())

        propSpecs.forEach {
            warn("add property: $it")
            classBuilder.addProperty(it)
        }

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