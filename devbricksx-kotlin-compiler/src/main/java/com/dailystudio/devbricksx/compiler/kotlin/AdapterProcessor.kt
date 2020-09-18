package com.dailystudio.devbricksx.compiler.kotlin

import com.dailystudio.devbricksx.annotations.Adapter
import com.dailystudio.devbricksx.annotations.ViewType
import com.dailystudio.devbricksx.compiler.kotlin.utils.AnnotationsUtils
import com.dailystudio.devbricksx.compiler.kotlin.utils.TypeNamesUtils
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic


@AutoService(Processor::class)
class AdapterProcessor : BaseProcessor() {

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Adapter::class.java.name)
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(Adapter::class.java)
                .forEach { element ->
                    if (element.kind != ElementKind.CLASS) {
                        error("Only classes can be annotated")
                        return true
                    }

                    if (element is TypeElement) {
                       val result = generateAdapter(element)

                        result?.let {
                            writeToFile(it)
                        }
                    }
                }


        return true
    }

    private fun generateAdapter(element: TypeElement) : GeneratedResult? {
        val typeName = element.simpleName.toString()
        var packageName = processingEnv.elementUtils.getPackageOf(element).toString()

        val annotation = element.getAnnotation(Adapter::class.java)
        val paged = annotation.paged
        val layout = annotation.layout
        val layoutByName = annotation.layoutByName
        val viewType = annotation.viewType

        val viewHolder = AnnotationsUtils.getClassValueFromAnnotation(element, "viewHolder")
                ?: return null

        val generatedClassName = GeneratedNames.getAdapterName(typeName)

        val objectTypeName = ClassName(packageName, typeName)
        val pagedListAdapter = TypeNamesUtils.getAbsPageListAdapterOfTypeName(objectTypeName, viewHolder)
        val listAdapter = TypeNamesUtils.getAbsListAdapterOfTypeName(objectTypeName, viewHolder)
        val itemCallback = TypeNamesUtils.getItemCallbackOfTypeName(objectTypeName)
        val diffUtils  = ClassName(packageName, GeneratedNames.getDiffUtilName(typeName))
        val viewGroup = TypeNamesUtils.getViewGroupTypeName()
        val layoutInflater = TypeNamesUtils.getLayoutInflaterTypeName()

        val classBuilder = TypeSpec.classBuilder(generatedClassName)
                .superclass(if (paged) pagedListAdapter else listAdapter)
                .addSuperclassConstructorParameter("DIFF_CALLBACK")
                .addModifiers(KModifier.OPEN)

        val classCompanionBuilder = TypeSpec.companionObjectBuilder();

        classCompanionBuilder.addProperty(PropertySpec.builder("DIFF_CALLBACK", itemCallback)
                .initializer("%T()", diffUtils)
                .build())

        classBuilder.addType(classCompanionBuilder.build())

        val methodOnCreateViewBuilder = FunSpec.builder("onCreateViewHolder")
                .addModifiers(KModifier.PUBLIC)
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("parent", viewGroup)
                .addParameter("viewType", Int::class)
                .addStatement("val layoutInflater = %T.from(parent.context)", layoutInflater)
                .returns(viewHolder)

        when (viewType) {
            ViewType.SingleLine -> {
                methodOnCreateViewBuilder.addStatement("val view = layoutInflater.inflate(%T.layout.list_item_single_line, null)",
                        TypeNamesUtils.getDevbrickxRTypeName())
            }
            ViewType.Card -> {
                methodOnCreateViewBuilder.addStatement("val view = layoutInflater.inflate(%T.layout.card_view, null)",
                        TypeNamesUtils.getDevbrickxRTypeName())
            }
            ViewType.CardInformative -> {
                methodOnCreateViewBuilder.addStatement("val view = layoutInflater.inflate(%T.layout.card_view_informative, null)",
                        TypeNamesUtils.getDevbrickxRTypeName())
            }
            ViewType.CardImmersive -> {
                methodOnCreateViewBuilder.addStatement("val view = layoutInflater.inflate(%T.layout.card_view_immersive, null)",
                        TypeNamesUtils.getDevbrickxRTypeName())
            }
            ViewType.Page -> {
                methodOnCreateViewBuilder.addStatement("val view = layoutInflater.inflate(%T.layout.page, null)",
                        TypeNamesUtils.getDevbrickxRTypeName())
                methodOnCreateViewBuilder.addStatement("view.layoutParams = %T(%T.MATCH_PARENT, %T.MATCH_PARENT)",
                        TypeNamesUtils.getViewGroupLayoutParameterTypeName(),
                        TypeNamesUtils.getViewGroupLayoutParameterTypeName(),
                        TypeNamesUtils.getViewGroupLayoutParameterTypeName())
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

        methodOnCreateViewBuilder.addStatement("return %T(view)", viewHolder)

        classBuilder.addFunction(methodOnCreateViewBuilder.build())

        val methodOnBindViewBuilder = FunSpec.builder("onBindViewHolder")
                .addModifiers(KModifier.PUBLIC)
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("holder", viewHolder)
                .addParameter("position", Int::class)
                .addStatement("super.onBindViewHolder(holder, position)")
                .addStatement("val item = getItem(position) ?: return")
                .addStatement("holder.bind(item)")

        classBuilder.addFunction(methodOnBindViewBuilder.build())

        return GeneratedResult(
                GeneratedNames.getAdapterPackageName(packageName),
                classBuilder)
    }

}