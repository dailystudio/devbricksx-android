package com.dailystudio.devbricksx.ksp.processors.step

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dailylstudio.devbricksx.annotations.plus.RoomCompanion
import com.dailystudio.devbricksx.ksp.GeneratedResult
import com.dailystudio.devbricksx.ksp.SingleSymbolProcessStep
import com.dailystudio.devbricksx.ksp.helper.GeneratedNames
import com.dailystudio.devbricksx.ksp.helper.lowerCamelCaseName
import com.dailystudio.devbricksx.ksp.helper.underlineCaseName
import com.dailystudio.devbricksx.ksp.processors.BaseSymbolProcessor
import com.dailystudio.devbricksx.ksp.utils.*
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toTypeName

class RoomCompanionStep (processor: BaseSymbolProcessor)
    : SingleSymbolProcessStep(RoomCompanion::class.qualifiedName!!, processor) {

    companion object {
        private const val METHOD_FROM_OBJECT = "fromObject"
        private const val METHOD_TO_OBJECT = "toObject"
        private const val METHOD_COPY_FIELDS_FROM_OBJECT = "copyFieldsFromObject"
        private const val METHOD_COPY_FIELDS_TO_OBJECT = "copyFieldsToObject"
    }

    override fun processSymbol(resolver: Resolver, symbol: KSClassDeclaration): GeneratedResult? {
        val typeName = symbol.typeName()
        val packageName = symbol.packageName()
        val superType = symbol.superClassType()
        warn("symbol: [pack: $packageName, type: $typeName], super: $superType")

        val typeNameToGenerate = GeneratedNames.getRoomCompanionName(typeName)

        val typeOfAny = TypeNameUtils.typeOfKotlinAny(resolver)

        val hasSuperType = (superType != typeOfAny)
        var supperTypeHasRoomCompanion = false
        var packageNameOfSuperType = ""
        var typeNameOfSuperType = ""
        val propsInSuperType = mutableSetOf<String>()
        val propsInConstructor = mutableSetOf<String>()
        val propsInSuperTypeConstructor = mutableSetOf<String>()
        if (hasSuperType) {
            val companionOfSuperType =
                superType.getAnnotation(RoomCompanion::class, resolver)
            warn("super roomCompanion: $companionOfSuperType")

            packageNameOfSuperType = superType.packageName()
            typeNameOfSuperType = superType.typeName()

            superType.getAllProperties().forEach {
                propsInSuperType.add(it.simpleName.getShortName())
            }

            superType.primaryConstructor?.parameters?.forEach { param ->
                val nameOfParam = param.name?.getShortName() ?: return@forEach
                propsInSuperTypeConstructor.add(nameOfParam)
            }

            supperTypeHasRoomCompanion = companionOfSuperType != null
        }
        warn("super type: [package: $packageNameOfSuperType, class: $typeNameOfSuperType], has companion = $supperTypeHasRoomCompanion")

        val roomCompanion = symbol.getAnnotation(RoomCompanion::class, resolver)
        warn("roomCompanion: ${roomCompanion?.arguments}")

        val primaryKeys: MutableList<String> =
            roomCompanion?.findArgument("primaryKeys") ?: mutableListOf()
        warn("primaryKeys: $primaryKeys")

        val constructorBuilder = FunSpec.constructorBuilder()

        symbol.primaryConstructor?.parameters?.forEach { param ->
            val nameOfParam = param.name?.getShortName() ?: return@forEach
            warn("processing constructor params: $nameOfParam")

            val paramSpecBuilder = ParameterSpec.builder(nameOfParam,
                param.type.toTypeName())

            constructorBuilder.addParameter(paramSpecBuilder.build())
            propsInConstructor.add(nameOfParam)
        }

        val propSpecs = mutableListOf<PropertySpec>()
        val primaryKeysFound = mutableSetOf<String>()
        for ((i, prop) in symbol.getAllProperties().withIndex()) {
            val nameOfProp = prop.simpleName.getShortName()
            val typeOfProp = prop.type.toTypeName()
            warn("processing prop [$i]: $prop [${typeOfProp}]")

            val propSpecBuilder = PropertySpec.builder(nameOfProp, typeOfProp)
                .addModifiers(KModifier.OPEN, KModifier.PUBLIC)
                .mutable(true) // must to mutable here for Room processing

            if (propsInSuperType.contains(nameOfProp)) {
                propSpecBuilder.addModifiers(KModifier.OVERRIDE)
            }

            if (propsInConstructor.contains(nameOfProp)) {
                propSpecBuilder.initializer(nameOfProp)
            } else {
                if (!prop.isMutable) {
                    error("prop [$nameOfProp] in [$symbol] is immutable. This blocks accessing from generated code. Please change to var or ignore with @Ignore")
                }

                val defaultVal = TypeNameUtils.defaultValOfTypeName(typeOfProp)
                warn("default val of [$typeOfProp]: $defaultVal")
                propSpecBuilder.initializer(defaultVal)
            }

            propSpecBuilder.addAnnotation(
                AnnotationSpec.builder(ColumnInfo::class)
                    .addMember("name = %S", nameOfProp.underlineCaseName())
                    .build()
            )

            if (primaryKeys.isEmpty() && i == 0) {
                warn("No primary key defined. using [$nameOfProp] by default")
                primaryKeys.add(nameOfProp)
            }

            if (primaryKeys.contains(nameOfProp)) {
                propSpecBuilder.addAnnotation(PrimaryKey::class)
                primaryKeysFound.add(nameOfProp)
            }

            propSpecs.add(propSpecBuilder.build())
        }

        if (primaryKeysFound != primaryKeys.toSet()) {
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

        val typeOfObject = TypeNameUtils.typeOfObject(packageName, typeName)
        val typeOfCompanion = TypeNameUtils.typeOfCompanion(packageName, typeName)
        val nameOfObjectParamOrVariable = typeName.lowerCamelCaseName()
        val nameOfCompanionParamOrVariable = typeNameToGenerate.lowerCamelCaseName()

        val classBuilder = TypeSpec.classBuilder(typeNameToGenerate)
            .addModifiers(KModifier.OPEN)
            .addAnnotation(entityAnnotationBuilder.build())
            .primaryConstructor(constructorBuilder.build())

        if (supperTypeHasRoomCompanion) {
            val typeOfSuperClass =
                TypeNameUtils.typeOfObject(packageNameOfSuperType,
                    GeneratedNames.getRoomCompanionName(typeNameOfSuperType))

            classBuilder.superclass(typeOfSuperClass)

            val strOfParams = propsInSuperTypeConstructor.joinToString(
                separator = ", "
            )

            if (strOfParams.isNotEmpty()) {
                classBuilder.addSuperclassConstructorParameter(strOfParams)
            }
        }

        propSpecs.forEach {
            classBuilder.addProperty(it)
        }

        val propsAll = propSpecs.map { it.name }.toSet()
        val propsInCurrentType =
            propsAll - propsInSuperType - propsInConstructor
        warn("props: all = $propsAll, from super = $propsInSuperType, constructor = $propsInConstructor")

        val classCompanionBuilder = TypeSpec.companionObjectBuilder()

        val methodCopyFieldsFromObject = FunSpec.builder(METHOD_COPY_FIELDS_FROM_OBJECT)
            .addModifiers(KModifier.PUBLIC)
            .addParameter(nameOfObjectParamOrVariable, typeOfObject)
        if (supperTypeHasRoomCompanion) {
            methodCopyFieldsFromObject.addStatement(
                "super.%N(%N)", METHOD_COPY_FIELDS_FROM_OBJECT,
                nameOfObjectParamOrVariable
            )
        }

        propsInCurrentType.forEach {
            methodCopyFieldsFromObject.addStatement(
                "this.%N = %N.%N",
                it, nameOfObjectParamOrVariable, it
            )
        }

        classBuilder.addFunction(methodCopyFieldsFromObject.build())

        val methodCopyFieldsToObject = FunSpec.builder(METHOD_COPY_FIELDS_TO_OBJECT)
            .addModifiers(KModifier.PUBLIC)
            .addParameter(nameOfObjectParamOrVariable, typeOfObject)
        if (supperTypeHasRoomCompanion) {
            methodCopyFieldsToObject.addStatement(
                "super.%N(%N)", METHOD_COPY_FIELDS_TO_OBJECT,
                nameOfObjectParamOrVariable
            )
        }

        propsInCurrentType.forEach {
            methodCopyFieldsToObject.addStatement(
                "%N.%N = this.%N",
                nameOfObjectParamOrVariable, it, it
            )
        }

        classBuilder.addFunction(methodCopyFieldsToObject.build())

        val methodFromObject = FunSpec.builder(METHOD_FROM_OBJECT)
            .addModifiers(KModifier.PUBLIC)
            .addParameter(nameOfObjectParamOrVariable, typeOfObject)
            .returns(typeOfCompanion)

        methodFromObject.addStatement(
            "val %N = %T(%L)",
            nameOfCompanionParamOrVariable,
            typeOfCompanion,
            propsInConstructor.joinToString(separator = ", ") {
                "${nameOfObjectParamOrVariable}.$it"
            }
        )
        methodFromObject.addStatement(
            "%N.%N(%N)",
            nameOfCompanionParamOrVariable,
            METHOD_COPY_FIELDS_FROM_OBJECT,
            nameOfObjectParamOrVariable
        )
        methodFromObject.addStatement("return %N",
            nameOfCompanionParamOrVariable)

        classCompanionBuilder.addFunction(methodFromObject.build())

        val methodToObject = FunSpec.builder(METHOD_TO_OBJECT)
            .addParameter(nameOfCompanionParamOrVariable, typeOfCompanion)
            .returns(typeOfObject)

        methodToObject.addStatement(
            "val %N = %T(%L)",
            nameOfObjectParamOrVariable,
            typeOfObject,
            propsInConstructor.joinToString(separator = ", ") {
                "${nameOfCompanionParamOrVariable}.$it"
            }
        )
        methodToObject.addStatement(
            "%N.%N(%N)",
            nameOfCompanionParamOrVariable,
            METHOD_COPY_FIELDS_TO_OBJECT,
            nameOfObjectParamOrVariable
        )
        methodToObject.addStatement("return %N",
            nameOfObjectParamOrVariable)

        classCompanionBuilder.addFunction(methodToObject.build())

        classBuilder.addType(classCompanionBuilder.build())

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