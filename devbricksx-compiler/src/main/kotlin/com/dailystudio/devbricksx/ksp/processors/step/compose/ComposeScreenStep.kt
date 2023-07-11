package com.dailystudio.devbricksx.ksp.processors.step.compose

import com.dailystudio.devbricksx.annotations.compose.Compose
import com.dailystudio.devbricksx.annotations.compose.ItemContent
import com.dailystudio.devbricksx.ksp.helper.GeneratedNames
import com.dailystudio.devbricksx.ksp.processors.BaseSymbolProcessor
import com.dailystudio.devbricksx.ksp.processors.GeneratedFunctionsResult
import com.dailystudio.devbricksx.ksp.processors.GeneratedResult
import com.dailystudio.devbricksx.ksp.processors.step.SingleSymbolProcessStep
import com.dailystudio.devbricksx.ksp.utils.*
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName

class ComposeScreenStep (processor: BaseSymbolProcessor)
    : SingleSymbolProcessStep(Compose::class, processor) {

    private val symbolsOfItemContent: MutableMap<ClassName, KSFunctionDeclaration> =
        mutableMapOf()

    override fun filterSymbols(
        resolver: Resolver,
        symbols: Sequence<KSClassDeclaration>
    ): Sequence<KSClassDeclaration> {
        return symbols.mapToShadowClass(resolver)
    }

    override fun preProcessSymbols(resolver: Resolver, symbols: Sequence<KSClassDeclaration>) {
        super.preProcessSymbols(resolver, symbols)

        val nameOfAnnotation = ItemContent::class.qualifiedName
        warn("looking up for [$nameOfAnnotation]")

        val foundSymbols = nameOfAnnotation?.let {
            resolver.getSymbolsWithAnnotation(nameOfAnnotation)
                .filterIsInstance<KSFunctionDeclaration>()
        }?: emptySequence()

        foundSymbols.forEach {
            val composeFor = it.getKSAnnotation(ItemContent::class, resolver)
                ?: return@forEach
            val typeOfEntity = composeFor.findArgument<KSType>("entity")
                .toClassName()

            warn("adding itemContent [${it.qualifiedName?.asString()}] for symbol : [${typeOfEntity}]")

            this.symbolsOfItemContent[typeOfEntity] = it
        }
    }

    override fun processSymbol(resolver: Resolver, symbol: KSClassDeclaration): List<GeneratedResult> {
        val typeName = symbol.typeName()
        val packageName = symbol.packageName()
        warn("symbol: [pack: $packageName, type: $typeName]")

        val funcNameOfScreen = GeneratedNames.getScreenName(typeName)
        val composePackageName = GeneratedNames.getComposePackageName(packageName)
        val annotation = symbol.getAnnotation(Compose::class, resolver) ?: return emptyResult

        val itemContent = symbolsOfItemContent[symbol.toClassName()] ?: return emptyResult
        warn("itemContent of this symbol: [${itemContent.qualifiedName?.toString()}]")
        val typeOfItemContent = ClassName.bestGuess(
            itemContent.qualifiedName?.asString() ?: return emptyResult
        )

        val itemContentParams = mutableListOf<ParameterSpec>()
        var itemContentParamsInvoke = ""
        itemContent.parameters.forEach {
            val type = it.type.resolve().toTypeName()
            val name = it.name?.asString() ?: return@forEach

            itemContentParams.add(
                ParameterSpec.builder(name, type).build()
            )

            itemContentParamsInvoke += "$name, "
        }

        itemContentParamsInvoke = itemContentParamsInvoke.removeSuffix(", ")

        val funcTypeOfItemContent = LambdaTypeName.get(
            parameters = itemContentParams,
            returnType = itemContent.returnType?.resolve()?.toTypeName() ?: UNIT
        ).copy(
            annotations = listOf(
                AnnotationSpec.builder(TypeNameUtils.typeOfComposable()).build()
            )
        )

        val itemContentParam = ParameterSpec.builder(
            name = "itemContent",
            funcTypeOfItemContent
        ).defaultValue(
            CodeBlock.of("{ %L -> %T(%L) }",
                itemContentParamsInvoke,
                typeOfItemContent,
                itemContentParamsInvoke
            )
        )

        val funcScreenSpec = FunSpec.builder(funcNameOfScreen)
            .addAnnotation(TypeNameUtils.typeOfComposable())
            .addParameter(itemContentParam.build())

        return listOf(
            GeneratedFunctionsResult(symbol,
                composePackageName,
                funcNameOfScreen,
                listOf(funcScreenSpec)),
        )
    }

}