package com.dailystudio.devbricksx.ksp.processors.step.view

import com.dailystudio.devbricksx.annotations.view.Adapter
import com.dailystudio.devbricksx.annotations.view.ViewType
import com.dailystudio.devbricksx.ksp.helper.GeneratedNames
import com.dailystudio.devbricksx.ksp.processors.BaseSymbolProcessor
import com.dailystudio.devbricksx.ksp.processors.GeneratedClassResult
import com.dailystudio.devbricksx.ksp.processors.GeneratedResult
import com.dailystudio.devbricksx.ksp.processors.step.SingleSymbolProcessStep
import com.dailystudio.devbricksx.ksp.utils.*
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toTypeName

class AdapterStep (processor: BaseSymbolProcessor)
    : SingleSymbolProcessStep(Adapter::class, processor) {

    companion object {
        const val PROP_OF_DIFF_UTIL = "diffCallback"
        const val DEFAULT_PROP_OF_DIFF_UTIL = "DIFF_CALLBACK"
    }

    override fun processSymbol(
        resolver: Resolver,
        symbol: KSClassDeclaration
    ): List<GeneratedResult> {
        val typeName = symbol.typeName()
        val packageName = symbol.packageName()

        val adapterAnnotation =
            symbol.getAnnotation(Adapter::class) ?: return emptyResult

        val adapterKSAnnotation =
            symbol.getKSAnnotation(Adapter::class, resolver) ?: return emptyResult

        val paged = adapterAnnotation.paged
        warn("[$symbol] adapter paged: $paged")
        val viewType = adapterAnnotation.viewType
        warn("[$symbol] viewType: $viewType")

        val layout = adapterAnnotation.layout
        val layoutByName = adapterAnnotation.layoutByName
        val notifyAfterListChanged = adapterAnnotation.notifyAfterListChanged

        val typeOfViewHolder = adapterKSAnnotation
            .findArgument<KSType>("viewHolder").toTypeName()
        val typeOfDiffUtil = adapterKSAnnotation
            .findArgument<KSType>("diffUtil").toTypeName()

        val typeNameToGenerate =
            GeneratedNames.getAdapterName(typeName)

        val typeOfObject = ClassName(packageName, typeName)
        val pagingDataAdapter = TypeNameUtils.typeOfAbsAbsPagingDataAdapterOf(
            typeOfObject, typeOfViewHolder)
        val listAdapter = TypeNameUtils.typeOfAbsListAdapterOf(
            typeOfObject, typeOfViewHolder)
        val itemCallback = TypeNameUtils.typeOfItemCallbackOf(typeOfObject)
        val diffUtils  = ClassName(packageName,
            GeneratedNames.getDiffUtilName(typeName))
        val viewGroup = TypeNameUtils.typeOfViewGroup()
        val layoutInflater = TypeNameUtils.typeOfLayoutInflater()

        val classBuilder = TypeSpec.classBuilder(typeNameToGenerate)
            .superclass(if (paged) {
                pagingDataAdapter
            } else {
                listAdapter
            })
            .primaryConstructor(
                FunSpec.constructorBuilder()
                .addParameter(
                    ParameterSpec.builder(PROP_OF_DIFF_UTIL, itemCallback)
                    .defaultValue(DEFAULT_PROP_OF_DIFF_UTIL)
                    .build())
                .build())
            .addSuperclassConstructorParameter(PROP_OF_DIFF_UTIL)
            .addModifiers(KModifier.OPEN)

        val classCompanionBuilder = TypeSpec.companionObjectBuilder();

        classCompanionBuilder.addProperty(
            PropertySpec.builder(DEFAULT_PROP_OF_DIFF_UTIL, itemCallback)
                .initializer("%T()",
                if (typeOfDiffUtil == UNIT) diffUtils else typeOfDiffUtil)
                .build())

        classBuilder.addType(classCompanionBuilder.build())

        val methodOnCreateViewBuilder = FunSpec.builder("onCreateViewHolder")
            .addModifiers(KModifier.PUBLIC)
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("parent", viewGroup)
            .addParameter("viewType", Int::class)
            .addStatement("val layoutInflater = %T.from(parent.context)", layoutInflater)
            .returns(typeOfViewHolder)

        when (viewType) {
            ViewType.SingleLine -> {
                methodOnCreateViewBuilder.addStatement("val view = layoutInflater.inflate(%T.layout.list_item_single_line, null)",
                    TypeNameUtils.typeOfDevBricksXR())
            }
            ViewType.Card -> {
                methodOnCreateViewBuilder.addStatement("val view = layoutInflater.inflate(%T.layout.card_view, null)",
                    TypeNameUtils.typeOfDevBricksXR())
            }
            ViewType.CardInformative -> {
                methodOnCreateViewBuilder.addStatement("val view = layoutInflater.inflate(%T.layout.card_view_informative, null)",
                    TypeNameUtils.typeOfDevBricksXR())
            }
            ViewType.CardImmersive -> {
                methodOnCreateViewBuilder.addStatement("val view = layoutInflater.inflate(%T.layout.card_view_immersive, null)",
                    TypeNameUtils.typeOfDevBricksXR())
            }
            ViewType.Page -> {
                methodOnCreateViewBuilder.addStatement("val view = layoutInflater.inflate(%T.layout.page, null)",
                    TypeNameUtils.typeOfDevBricksXR())
                methodOnCreateViewBuilder.addStatement("view.layoutParams = %T(%T.MATCH_PARENT, %T.MATCH_PARENT)",
                    TypeNameUtils.typeOfViewGroupLayoutParameter(),
                    TypeNameUtils.typeOfViewGroupLayoutParameter(),
                    TypeNameUtils.typeOfViewGroupLayoutParameter())
            }
            else -> {
                if (layoutByName.isNotBlank()) {
                    methodOnCreateViewBuilder.addStatement(
                        "val layoutId = parent.context.resources.getIdentifier(\"%L\", " +
                                "\"layout\", " +
                                "parent.context.packageName)", layoutByName)
                    methodOnCreateViewBuilder.addStatement("val view = layoutInflater.inflate(layoutId, null)")
                } else {
                    methodOnCreateViewBuilder.addStatement("val view = layoutInflater.inflate(%L, null)", layout)
                }
            }
        }

        methodOnCreateViewBuilder.addStatement("return %T(view)", typeOfViewHolder)

        classBuilder.addFunction(methodOnCreateViewBuilder.build())

        val methodOnBindViewBuilder = FunSpec.builder("onBindViewHolder")
            .addModifiers(KModifier.PUBLIC)
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("holder", typeOfViewHolder)
            .addParameter("position", Int::class)
            .addStatement("super.onBindViewHolder(holder, position)")
            .addStatement("val item = getItem(position) ?: return")
            .addStatement("holder.bind(item)")

        classBuilder.addFunction(methodOnBindViewBuilder.build())

        if (notifyAfterListChanged && !paged) {
            val pagedListOfObjects =
                TypeNameUtils.typeOfPagedListOf(typeOfObject).copy(nullable = true)
            val mutableListOfObjects =
                TypeNameUtils.typeOfMutableListOf(typeOfObject)

            val methodOnCurrentListChanged = FunSpec.builder("onCurrentListChanged")
                .addModifiers(KModifier.PUBLIC)
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("previousList", if (paged) pagedListOfObjects else mutableListOfObjects)
                .addParameter("currentList", if (paged) pagedListOfObjects else mutableListOfObjects)
                .addStatement("super.onCurrentListChanged(previousList, currentList)")
                .addStatement("notifyDataSetChanged()")

            classBuilder.addFunction(methodOnCurrentListChanged.build())
        }

        return singleClassResult(symbol,
            GeneratedNames.getAdapterPackageName(packageName),
            classBuilder)
    }

}