package com.dailystudio.devbricksx.ksp.processors.step

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dailystudio.devbricksx.annotations.data.RoomCompanion
import com.dailystudio.devbricksx.ksp.GeneratedResult
import com.dailystudio.devbricksx.ksp.SingleSymbolProcessStep
import com.dailystudio.devbricksx.ksp.helper.*
import com.dailystudio.devbricksx.ksp.processors.BaseSymbolProcessor
import com.dailystudio.devbricksx.ksp.utils.*
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName

class RoomCompanionStep (processor: BaseSymbolProcessor)
    : SingleSymbolProcessStep(RoomCompanion::class, processor) {

    companion object {
        private const val METHOD_FROM_OBJECT = "fromObject"
        private const val METHOD_TO_OBJECT = "toObject"
        private const val METHOD_COPY_FIELDS_FROM_OBJECT = "copyFieldsFromObject"
        private const val METHOD_COPY_FIELDS_TO_OBJECT = "copyFieldsToObject"

        private const val PROP_NAME_MAP_TO_OBJECT = "mapCompanionToObject"
        private const val PROP_NAME_MAP_TO_OBJECTS = "mapCompanionsToObjects"
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

        val allProperties = symbol.getAllProperties()

        val propsAll = allProperties.map { it.simpleName.getShortName() }.toSet()
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

        val primaryKeys: Set<String> =
//            roomCompanion?.findArgument("primaryKeys") ?: mutableListOf()
            RoomPrimaryKeysUtils.findPrimaryKeyNames(symbol, resolver)
        warn("primaryKeys: $primaryKeys")

        val foreignKeys: MutableList<KSAnnotation> =
            roomCompanion?.findArgument("foreignKeys") ?: mutableListOf()
        warn("foreignKeys: $foreignKeys")

        val autoGenerate: Boolean =
            roomCompanion?.findArgument("autoGenerate") ?: false
        warn("autoGenerate: $autoGenerate")

        val indices: MutableList<KSAnnotation> =
            roomCompanion?.findArgument("indices") ?: mutableListOf()
        warn("indices: $indices")

        val constructorBuilder = FunSpec.constructorBuilder()

        symbol.primaryConstructor?.parameters?.forEach { param ->
            val nameOfParam = param.name?.getShortName() ?: return@forEach
            warn("processing constructor params: $nameOfParam")

            if (!param.isVal && !param.isVar) {
                error("For @RoomCompanion annotated class [$symbol], only val or var is supported in primary constructor params: $nameOfParam")
            }

            val paramSpecBuilder = ParameterSpec.builder(nameOfParam,
                param.type.toTypeName())

            constructorBuilder.addParameter(paramSpecBuilder.build())
            propsInConstructor.add(nameOfParam)
        }

        val propSpecs = mutableListOf<PropertySpec>()
        val primaryKeysFound = mutableSetOf<String>()
        for ((i, prop) in allProperties.withIndex()) {
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

        if (foreignKeys.isNotEmpty()) {
            warn("processing: foreign keys [$foreignKeys]")

            val strOfForeignKeys = foreignKeys.joinToString(separator = ",") {
                val annotationSpec = it.toAnnotationSpec()

                val entity = it.findArgument<KSType>("entity").toClassName()
                val packageNameOfEntity = entity.packageName
                val typeNameOfEntity = entity.simpleName

                warn("processing: foreign key of [$packageNameOfEntity, $typeNameOfEntity]")
                annotationSpec.toString()
                    .replace(typeNameOfEntity, GeneratedNames.getRoomCompanionName(typeNameOfEntity))
                    .removePrefix("@")
            }
            warn("processing: new foreign keys str = [$strOfForeignKeys]")

            entityAnnotationBuilder.addMember("foreignKeys = [%L]", strOfForeignKeys)
        }

        if (indices.isNotEmpty()) {
            warn("processing: indices [$indices]")

            val strOfIndices = indices.joinToString(separator = ",") {
                val annotationSpec = it.toAnnotationSpec()

                annotationSpec.toString()
                    .removePrefix("@")
            }
            warn("processing: new indices str = [$strOfIndices]")

            entityAnnotationBuilder.addMember("indices = [%L]", strOfIndices)
        }

        val typeOfObject = TypeNameUtils.typeOfObject(packageName, typeName)
        val typeOfCompanion = TypeNameUtils.typeOfCompanion(packageName, typeName)
        val typeOfListOfObjects = TypeNameUtils.typeOfListOf(typeOfObject)
        val typeOfListOfCompanions = TypeNameUtils.typeOfListOf(typeOfCompanion)
        val nameOfObject = typeName.toVariableOrParamName()
        val nameOfCompanion = typeNameToGenerate.toVariableOrParamName()
        val nameOfCompanions = typeNameToGenerate.toVariableOrParamNameOfCollection()

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

        val propsInCurrentType =
            propsAll - propsInSuperType - propsInConstructor
        warn("props: all = $propsAll, from super = $propsInSuperType, constructor = $propsInConstructor")

        val classCompanionBuilder = TypeSpec.companionObjectBuilder()

        val methodCopyFieldsFromObject = FunSpec.builder(METHOD_COPY_FIELDS_FROM_OBJECT)
            .addModifiers(KModifier.PUBLIC)
            .addParameter(nameOfObject, typeOfObject)
        if (supperTypeHasRoomCompanion) {
            methodCopyFieldsFromObject.addStatement(
                "super.%N(%N)", METHOD_COPY_FIELDS_FROM_OBJECT,
                nameOfObject
            )
        }

        propsInCurrentType.forEach {
            methodCopyFieldsFromObject.addStatement(
                "this.%N = %N.%N",
                it, nameOfObject, it
            )
        }

        classBuilder.addFunction(methodCopyFieldsFromObject.build())

        val methodCopyFieldsToObject = FunSpec.builder(METHOD_COPY_FIELDS_TO_OBJECT)
            .addModifiers(KModifier.PUBLIC)
            .addParameter(nameOfObject, typeOfObject)
        if (supperTypeHasRoomCompanion) {
            methodCopyFieldsToObject.addStatement(
                "super.%N(%N)", METHOD_COPY_FIELDS_TO_OBJECT,
                nameOfObject
            )
        }

        propsInCurrentType.forEach {
            methodCopyFieldsToObject.addStatement(
                "%N.%N = this.%N",
                nameOfObject, it, it
            )
        }

        classBuilder.addFunction(methodCopyFieldsToObject.build())

        val methodFromObjectBuilder = FunSpec.builder(METHOD_FROM_OBJECT)
            .addModifiers(KModifier.PUBLIC)
            .addParameter(nameOfObject, typeOfObject)
            .returns(typeOfCompanion)

        methodFromObjectBuilder.addStatement(
            "val %N = %T(%L)",
            nameOfCompanion,
            typeOfCompanion,
            propsInConstructor.joinToString(separator = ", ") {
                "${nameOfObject}.$it"
            }
        )
        methodFromObjectBuilder.addStatement(
            "%N.%N(%N)",
            nameOfCompanion,
            METHOD_COPY_FIELDS_FROM_OBJECT,
            nameOfObject
        )
        methodFromObjectBuilder.addStatement("return %N",
            nameOfCompanion)

        classCompanionBuilder.addFunction(methodFromObjectBuilder.build())

        val methodToObjectBuilder = FunSpec.builder(METHOD_TO_OBJECT)
            .addModifiers(KModifier.OPEN)
            .returns(typeOfObject)

        if (supperTypeHasRoomCompanion) {
            methodToObjectBuilder.addModifiers(KModifier.OVERRIDE)
        }

        methodToObjectBuilder.addStatement(
            "val %N = %T(%L)",
            nameOfObject,
            typeOfObject,
            propsInConstructor.joinToString(separator = ", ")
        )
        methodToObjectBuilder.addStatement(
            "%N(%N)",
            METHOD_COPY_FIELDS_TO_OBJECT,
            nameOfObject
        )
        methodToObjectBuilder.addStatement("return %N",
            nameOfObject)

        classBuilder.addFunction(methodToObjectBuilder.build())

        val propMapToObjectBuilder = PropertySpec.builder(PROP_NAME_MAP_TO_OBJECT,
            TypeNameUtils.typeOfMapFunction(typeOfCompanion, typeOfObject))
            .initializer("""
                object : Function<%T, %T> {
                    override fun apply(%N: %T): %T {
                        return %N.toObject()
                    }
                }
            """.trimIndent(),
                typeOfCompanion, typeOfObject,
                nameOfCompanion, typeOfCompanion, typeOfObject,
                nameOfCompanion
            )

        classCompanionBuilder.addProperty(propMapToObjectBuilder.build())

        val propMapToObjectsBuilder = PropertySpec.builder(PROP_NAME_MAP_TO_OBJECTS,
            TypeNameUtils.typeOfMapFunction(typeOfListOfCompanions, typeOfListOfObjects))
            .initializer("""
                object : Function<%T, %T> {
                    override fun apply(%N: %T): %T {
                        return %N.map { it.toObject() }
                    }
                }
            """.trimIndent(),
                typeOfListOfCompanions, typeOfListOfObjects,
                nameOfCompanions, typeOfListOfCompanions, typeOfListOfObjects,
                nameOfCompanions
            )

        classCompanionBuilder.addProperty(propMapToObjectsBuilder.build())

        classBuilder.addType(classCompanionBuilder.build())

        return GeneratedResult(packageName, classBuilder)
    }

    private fun buildPrimaryKeysString(primaryKeys: Set<String>): String {
        val prKeysBuilder = StringBuilder("{ ")
        for ((i, primaryKey) in primaryKeys.withIndex()) {
            prKeysBuilder.append("\"")
            prKeysBuilder.append(primaryKey)
            prKeysBuilder.append("\"")
            if (i < primaryKeys.size - 1) {
                prKeysBuilder.append(", ")
            }
        }
        prKeysBuilder.append(" }")
        return prKeysBuilder.toString()
    }

}