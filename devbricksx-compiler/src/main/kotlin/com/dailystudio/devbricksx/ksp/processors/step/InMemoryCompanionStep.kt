package com.dailystudio.devbricksx.ksp.processors.step

import com.dailystudio.devbricksx.annotations.data.InMemoryCompanion
import com.dailystudio.devbricksx.annotations.data.Ordering
import com.dailystudio.devbricksx.ksp.GeneratedResult
import com.dailystudio.devbricksx.ksp.SingleSymbolProcessStep
import com.dailystudio.devbricksx.ksp.helper.GeneratedNames
import com.dailystudio.devbricksx.ksp.processors.BaseSymbolProcessor
import com.dailystudio.devbricksx.ksp.utils.*
import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName

class InMemoryCompanionStep (processor: BaseSymbolProcessor)
    : SingleSymbolProcessStep(InMemoryCompanion::class, processor) {

    override fun processSymbol(resolver: Resolver, symbol: KSClassDeclaration): List<GeneratedResult> {
        val typeName = symbol.typeName()
        val packageName = symbol.packageName()
        warn("symbol: [pack: $packageName, type: $typeName]")

        val typeNameOfManager = GeneratedNames.getManagerName(typeName)
        val typeNameOfRepository = GeneratedNames.getRepositoryName(typeName)

        val annotation =
            symbol.getAnnotation(InMemoryCompanion::class, resolver)

        val pageSize = annotation?.findArgument<Int>("pageSize")
            ?: InMemoryCompanion.DEFAULT_PAGE_SIZE
        val ordering = try {
            Ordering.valueOf(annotation?.findArgument<KSType>("ordering")
                ?.declaration?.toString() ?: Ordering.Ascending.toString())
        } catch (e: Exception) {
            error("get ordering failed of [$symbol]: $e")

            Ordering.Ascending
        }
        warn("ordering: $ordering")

        val typeOfInMemoryObject = TypeNameUtils.typeOfInMemoryObject()
        val found = findClassInSuperTypes(resolver, symbol, typeOfInMemoryObject)
        warn("found class of [$typeOfInMemoryObject] in symbol [${symbol}]: $found ")
        if (found == null) {
            error("InMemoryCompanion can ONLY annotate on derived classes of InMemoryObject.")

            return emptyResult
        }

        val typeOfKey = found.arguments.first().toTypeName()
        warn("type of key in [$typeOfInMemoryObject]: $typeOfKey")

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
            GeneratedResult(packageName, classBuilderOfManager),
            GeneratedResult(
                GeneratedNames.getRepositoryPackageName(packageName),
                classBuilderOfRepo)
        )
    }

    private fun findClassInSuperTypes(resolver: Resolver,
                                      symbol: KSClassDeclaration,
                                      className: ClassName): KSType? {
        symbol.getAllSuperTypes().forEach {
            val classNameOfSupertype = it.toClassName()
            warn("find class [$className]: super type [$classNameOfSupertype] of $symbol")
            if (classNameOfSupertype == className) {
                return it
            }
        }

        return null
    }

}