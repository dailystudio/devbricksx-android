package com.dailystudio.devbricksx.compiler.kotlin

import com.dailystudio.devbricksx.annotations.FragmentAdapter
import com.dailystudio.devbricksx.compiler.kotlin.utils.AnnotationsUtils
import com.dailystudio.devbricksx.compiler.kotlin.utils.TypeNamesUtils
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement


@AutoService(Processor::class)
class FragmentAdapterProcessor : BaseProcessor() {

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(FragmentAdapter::class.java.name)
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(FragmentAdapter::class.java)
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

        val annotation = element.getAnnotation(FragmentAdapter::class.java)
        val pageFragment = AnnotationsUtils.getClassValueFromAnnotation(element, "pageFragment")
                ?: return null

        val generatedClassName = GeneratedNames.getFragmentAdapterName(typeName)

        val objectTypeName = ClassName(packageName, typeName)
        val fragmentAdapter = TypeNamesUtils.getAbsFragmentStateAdapterOfTypeName(objectTypeName)
        val itemCallback = TypeNamesUtils.getItemCallbackOfTypeName(objectTypeName)
        val diffUtils  = ClassName(packageName, GeneratedNames.getDiffUtilName(typeName))
        val fragmentManager = TypeNamesUtils.getFragmentManagerTypeName()
        val lifecycle = TypeNamesUtils.getLifecycleTypeName()

        val classBuilder = TypeSpec.classBuilder(generatedClassName)
                .superclass(fragmentAdapter)
                .primaryConstructor(FunSpec.constructorBuilder()
                        .addParameter("fragmentManager", fragmentManager)
                        .addParameter("lifecycle", lifecycle).build())
                .addSuperclassConstructorParameter("DIFF_CALLBACK")
                .addSuperclassConstructorParameter("fragmentManager")
                .addSuperclassConstructorParameter("lifecycle")
                .addModifiers(KModifier.OPEN)

        val classCompanionBuilder = TypeSpec.companionObjectBuilder();

        classCompanionBuilder.addProperty(PropertySpec.builder("DIFF_CALLBACK", itemCallback)
                .initializer("%T()", diffUtils)
                .build())

        classBuilder.addType(classCompanionBuilder.build())

        val methodOnCreateViewBuilder = FunSpec.builder("onCreateFragment")
                .addModifiers(KModifier.PUBLIC)
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("item", objectTypeName)
                .addStatement("return %T(item)", pageFragment)
                .returns(pageFragment)

        classBuilder.addFunction(methodOnCreateViewBuilder.build())

        return GeneratedResult(
                GeneratedNames.getAdapterPackageName(packageName),
                classBuilder)
    }

}