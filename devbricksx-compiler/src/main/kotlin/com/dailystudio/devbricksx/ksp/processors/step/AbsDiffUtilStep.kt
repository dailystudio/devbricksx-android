package com.dailystudio.devbricksx.ksp.processors.step

import com.dailystudio.devbricksx.annotations.data.InMemoryCompanion
import com.dailystudio.devbricksx.annotations.data.RoomCompanion
import com.dailystudio.devbricksx.ksp.GeneratedResult
import com.dailystudio.devbricksx.ksp.SingleSymbolProcessStep
import com.dailystudio.devbricksx.ksp.helper.GeneratedNames
import com.dailystudio.devbricksx.ksp.processors.BaseSymbolProcessor
import com.dailystudio.devbricksx.ksp.utils.*
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import kotlin.reflect.KClass

abstract class AbsDiffUtilStep(classOfAnnotation: KClass<out Annotation>,
                               processor: BaseSymbolProcessor
) : SingleSymbolProcessStep(classOfAnnotation, processor) {

    override fun processSymbol(
        resolver: Resolver,
        symbol: KSClassDeclaration
    ): List<GeneratedResult> {
        val typeName = symbol.typeName()
        val packageName = symbol.packageName()
        warn("diff for: [pack: $packageName, type: $typeName]")

        val typeNameToGenerate = GeneratedNames.getDiffUtilName(typeName)
        val typeOfObject = ClassName(packageName, typeName)

        val isRoomCompanion = (symbol.getAnnotation(RoomCompanion::class, resolver) != null)
        val isInMemoryCompanion = (symbol.getAnnotation(InMemoryCompanion::class, resolver) != null)
        warn("check companion: isRoomCompanion = $isRoomCompanion, isInMemoryCompanion = $isInMemoryCompanion")
        if (!isRoomCompanion && !isInMemoryCompanion) {
            error("@Adapter only support types annotated with @RoomCompanion or @InMemoryCompanion")

            return emptyResult
        }

        val itemCallbackTypeName = TypeNameUtils
            .typeOfItemCallbackOf(typeOfObject)

        val classBuilder = TypeSpec.classBuilder(typeNameToGenerate)
            .superclass(itemCallbackTypeName)
            .addModifiers(KModifier.OPEN)

        val methodItemsSameBuilder: FunSpec.Builder = FunSpec.builder("areItemsTheSame")
            .addModifiers(KModifier.PUBLIC)
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("oldObject", typeOfObject)
            .addParameter("newObject", typeOfObject)
            .returns(Boolean::class)

        val methodContentsSameBuilder: FunSpec.Builder = FunSpec.builder("areContentsTheSame")
            .addModifiers(KModifier.PUBLIC)
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("oldObject", typeOfObject)
            .addParameter("newObject", typeOfObject)
            .returns(Boolean::class)

        attachEqualsStatements(resolver, symbol, methodItemsSameBuilder, methodContentsSameBuilder)

        classBuilder.addFunction(methodItemsSameBuilder.build())
        classBuilder.addFunction(methodContentsSameBuilder.build())

        return singleResult(packageName, classBuilder)
    }

    protected abstract fun attachEqualsStatements(resolver: Resolver,
                                                  symbol: KSClassDeclaration,
                                                  methodItemsSameBuilder: FunSpec.Builder,
                                                  methodContentsSameBuilder: FunSpec.Builder)

}