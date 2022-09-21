package com.dailystudio.devbricksx.ksp.processors.step.data

import com.dailystudio.devbricksx.annotations.data.InMemoryCompanion
import com.dailystudio.devbricksx.annotations.data.Ordering
import com.dailystudio.devbricksx.ksp.helper.GeneratedNames
import com.dailystudio.devbricksx.ksp.processors.BaseSymbolProcessor
import com.dailystudio.devbricksx.ksp.processors.GeneratedResult
import com.dailystudio.devbricksx.ksp.processors.step.SingleSymbolProcessStep
import com.dailystudio.devbricksx.ksp.utils.*
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.*

class InMemoryCompanionStep (processor: BaseSymbolProcessor)
    : SingleSymbolProcessStep(InMemoryCompanion::class, processor) {

    override fun processSymbol(resolver: Resolver, symbol: KSClassDeclaration): List<GeneratedResult> {
        val typeName = symbol.typeName()
        val packageName = symbol.packageName()
        warn("symbol: [pack: $packageName, type: $typeName]")

        val typeNameOfManager = GeneratedNames.getManagerName(typeName)
        val typeNameOfRepository = GeneratedNames.getRepositoryName(typeName)

        val annotation = symbol.getAnnotation(InMemoryCompanion::class) ?: return emptyResult

        val pageSize = annotation.pageSize
        val ordering = annotation.ordering
        warn("ordering: $ordering")

        val typeOfKey = InMemoryCompanionUtils.getKeyForInMemoryObject(symbol)
        warn("key of type[$symbol]: $typeOfKey")
        if (typeOfKey == null) {
            error("InMemoryCompanion can ONLY annotate on derived classes of InMemoryObject.")

            return emptyResult
        }

        val typeOfObject = ClassName(packageName, typeName)
        val typeOfManager = TypeNameUtils.typeOfInMemoryObjectManagerOf(
            typeOfKey, typeOfObject)
        val typeOfGeneratedManager = ClassName(packageName, typeNameOfManager)
        val typeOfRepo = TypeNameUtils.typeOfObjectRepositoryOf(
            typeOfKey, typeOfObject)
        val typeOfListObjects = TypeNameUtils.typeOfListOf(typeOfObject)

        val classBuilderOfManager = TypeSpec.objectBuilder(typeNameOfManager)
            .superclass(typeOfManager)

        val sortFuncName =
            if (ordering == Ordering.Ascending) {
                "sortedBy"
            } else {
                "sortedByDescending"
            }

        val sortListMethod = FunSpec.builder("sortList")
            .addParameter("objects", typeOfListObjects)
            .addModifiers(KModifier.OVERRIDE)
            .addCode("return objects.%N {\n" +
                    "   it.getKey()\n" +
                    "}", sortFuncName)

        classBuilderOfManager.addFunction(sortListMethod.build())

        val classBuilderOfRepo = TypeSpec.classBuilder(typeNameOfRepository)
            .superclass(typeOfRepo)
            .addSuperclassConstructorParameter("%T", typeOfGeneratedManager)
            .addSuperclassConstructorParameter("%L", pageSize)
            .addModifiers(KModifier.OPEN)

        return listOf(
            GeneratedResult(symbol, packageName, classBuilderOfManager),
            GeneratedResult(symbol,
                GeneratedNames.getRepositoryPackageName(packageName),
                classBuilderOfRepo)
        )
    }

}