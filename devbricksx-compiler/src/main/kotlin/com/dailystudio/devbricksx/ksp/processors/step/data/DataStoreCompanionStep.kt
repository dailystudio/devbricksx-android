package com.dailystudio.devbricksx.ksp.processors.step.data

import com.dailystudio.devbricksx.annotations.data.DataStoreCompanion
import com.dailystudio.devbricksx.annotations.data.StoreType
import com.dailystudio.devbricksx.ksp.helper.GeneratedNames
import com.dailystudio.devbricksx.ksp.helper.capitalizeName
import com.dailystudio.devbricksx.ksp.helper.kebabCaseName
import com.dailystudio.devbricksx.ksp.processors.BaseSymbolProcessor
import com.dailystudio.devbricksx.ksp.processors.GeneratedResult
import com.dailystudio.devbricksx.ksp.processors.step.SingleSymbolProcessStep
import com.dailystudio.devbricksx.ksp.utils.*
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toTypeName
import org.jetbrains.annotations.Nullable

class DataStoreCompanionStep (processor: BaseSymbolProcessor)
    : SingleSymbolProcessStep(DataStoreCompanion::class, processor)  {

    override fun processSymbol(
        resolver: Resolver,
        symbol: KSClassDeclaration
    ): List<GeneratedResult> {
        val dataStoreCompanion = symbol
            .getAnnotation(DataStoreCompanion::class) ?: return emptyResult

        return when (dataStoreCompanion.storeType) {
            StoreType.SharedPreference -> generateSharedPreference(resolver, symbol)
            else -> emptyResult
        }
    }

    private fun generateSharedPreference(resolver: Resolver,
                                         symbol: KSClassDeclaration): List<GeneratedResult> {
        val typeName = symbol.typeName()
        val packageName = symbol.packageName()
        val superSymbol = symbol.superClassType()
        warn("symbol: [pack: $packageName, type: $typeName], super: $superSymbol")

        val typeNameToGenerate =
            GeneratedNames.getSharedPrefsName(typeName)

        val typeOfSharedPreference = ClassName(packageName, typeNameToGenerate)
        val absPrefs = TypeNameUtils.typeOfAbsPrefs()
        val globalContextWrapper =
            TypeNameUtils.typeOfGlobalContextWrapper()
        val prefName = typeName.kebabCaseName()

        val supperTypeHasSharedPreference =
            superSymbol.hasAnnotation(DataStoreCompanion::class)

        val superType = if (supperTypeHasSharedPreference) {
            val packageNameOfSuperType = superSymbol.packageName()
            val typeNameOfSuperType = superSymbol.typeName()

            ClassName(packageNameOfSuperType,
                GeneratedNames.getSharedPrefsName(typeNameOfSuperType))
        } else {
            absPrefs
        }

        val classBuilder = TypeSpec.classBuilder(typeNameToGenerate)
            .superclass(superType)
            .addModifiers(KModifier.OPEN)

        val compObjBuilder = TypeSpec.companionObjectBuilder()

        val prefNameFieldBuilder = PropertySpec.builder("prefName", String::class)
            .addModifiers(KModifier.OVERRIDE)
            .initializer("%S", prefName)

        val instanceFieldBuilder =
            PropertySpec.builder("instance", typeOfSharedPreference)
                .initializer("%T()", typeOfSharedPreference)

        compObjBuilder.addProperty(instanceFieldBuilder.build())

        val allProps = symbol.getAllProperties().map {
            it.simpleName.getShortName()
        }.toSet()
        val propsInSuperSymbol = superSymbol.getAllProperties().map {
            it.simpleName.getShortName()
        }.toSet()
        val propsInSymbol = allProps - propsInSuperSymbol

        warn("SP.allProps: $allProps")
        warn("SP.propsInSuperSymbol: $propsInSuperSymbol")
        warn("SP.propsInSymbol: $propsInSymbol")

        symbol.getAllProperties().filter {
            propsInSymbol.contains(it.simpleName.getShortName())
        }.forEach {
            val nameOfProp: String = it.simpleName.getShortName()
            val nameOfKey: String = nameOfProp.kebabCaseName()
            val nameOfPrefKey: String = GeneratedNames.getPreferenceKeyName(nameOfProp)

            val nullable = it.hasAnnotation(Nullable::class)

            val typeOfProp = it.type.toTypeName().copy(nullable = nullable)
            val defaultVal = TypeNameUtils.defaultValOfType(typeOfProp)
            val absPrefFuncType = it.type.toAbsPrefFuncType()
            if (absPrefFuncType == null) {
                error("unsupported shared preference type [${it.type.toTypeName()}] in [$symbol]")
                return emptyResult
            }

            val prefNameConstant = PropertySpec.builder(nameOfPrefKey, String::class)
                .addModifiers(KModifier.CONST)
                .initializer("%S", nameOfKey)

            compObjBuilder.addProperty(prefNameConstant.build())

            val propBuilder = PropertySpec.builder(nameOfProp, typeOfProp)
                .mutable(true)

            val getterBuilder = FunSpec.getterBuilder()
                .addStatement("val defaultVal = %L", defaultVal)
                .addStatement("val context = %L.context ?: return defaultVal", globalContextWrapper)
            if (absPrefFuncType.second) {
                getterBuilder.addStatement("return get%LPrefValue(context, %S) ?: defaultVal",
                    absPrefFuncType.first,
                    nameOfKey)
            } else {
                getterBuilder.addStatement("return get%LPrefValue(context, %S, defaultVal)",
                    absPrefFuncType.first,
                    nameOfKey)
            }

            val setterBuilder = FunSpec.setterBuilder()
                .addParameter("value", typeOfProp)
                .addStatement("field = value")
                .addStatement("val context = %L.context ?: return", globalContextWrapper)
                .addStatement("set%LPrefValue(context, %S, value)",
                    absPrefFuncType.first, nameOfKey)

            val initializerBuilder = CodeBlock.builder()
                .addStatement("%L", defaultVal)

            propBuilder.getter(getterBuilder.build())
            propBuilder.setter(setterBuilder.build())
            propBuilder.initializer(initializerBuilder.build())

            classBuilder.addProperty(propBuilder.build())
        }

        classBuilder.addType(compObjBuilder.build())
        classBuilder.addProperty(prefNameFieldBuilder.build())

        return singleResult(symbol,
            packageName,
            classBuilder
        )
    }

    private fun generateDataStore(resolver: Resolver,
                                  symbol: KSClassDeclaration): List<GeneratedResult> {
        return emptyResult
    }

    private fun KSTypeReference.toAbsPrefFuncType(): Pair<String, Boolean>? {
        return when (resolve().declaration.simpleName.getShortName()) {
            "Int" -> Pair("Integer", false)
            "String" -> Pair("String", true)
            "Float" -> Pair("Float", false)
            "Long" -> Pair("Long", false)
            "Boolean" -> Pair("Boolean", false)
            else -> null
        }
    }
}