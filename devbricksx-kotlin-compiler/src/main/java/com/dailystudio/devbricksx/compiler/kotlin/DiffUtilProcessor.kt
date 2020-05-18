package com.dailystudio.devbricksx.compiler.kotlin

import com.dailystudio.devbricksx.annotations.DiffUtil
import com.dailystudio.devbricksx.compiler.kotlin.utils.TypeNamesUtils
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic


@AutoService(Processor::class)
class DiffUtilProcessor : BaseProcessor() {

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(DiffUtil::class.java.name)
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(DiffUtil::class.java)
                .forEach { element ->
                    if (element.kind != ElementKind.CLASS) {
                        error("Only classes can be annotated")
                        return true
                    }

                    if (element is TypeElement) {
                       val result = generateInMemoryRepository(element)

                        result?.let {
                            writeToFile(it)
                        }
                    }
                }


        return true
    }

    private fun generateInMemoryRepository(element: TypeElement) : GeneratedResult? {
        val typeName = element.simpleName.toString()

        var packageName = processingEnv.elementUtils.getPackageOf(element).toString()

        val generatedClassName = GeneratedNames.getDiffUtilName(typeName)
        val objectTypeName = ClassName(packageName, typeName)
        val itemCallbackTypeName = TypeNamesUtils.getItemCallbackOfTypeName(objectTypeName)

        val classBuilder = TypeSpec.classBuilder(generatedClassName)
                .superclass(itemCallbackTypeName)
                .addModifiers(KModifier.OPEN)

        val methodItemsSameBuilder: FunSpec.Builder = FunSpec.builder("areItemsTheSame")
                .addModifiers(KModifier.PUBLIC)
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("oldObject", objectTypeName)
                .addParameter("newObject", objectTypeName)
                .returns(Boolean::class)

        methodItemsSameBuilder.addStatement("return (newObject.getKey() == oldObject.getKey())")

        classBuilder.addFunction(methodItemsSameBuilder.build())

        val methodContentsSameBuilder: FunSpec.Builder = FunSpec.builder("areContentsTheSame")
                .addModifiers(KModifier.PUBLIC)
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("oldObject", objectTypeName)
                .addParameter("newObject", objectTypeName)
                .returns(Boolean::class)

        methodContentsSameBuilder.addStatement("return (newObject == oldObject)")

        classBuilder.addFunction(methodContentsSameBuilder.build())

        return GeneratedResult(
                packageName,
                classBuilder)
    }

}