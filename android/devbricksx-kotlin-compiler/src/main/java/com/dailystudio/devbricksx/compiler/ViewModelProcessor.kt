package com.dailystudio.devbricksx.compiler

import com.dailystudio.devbricksx.annotations.ViewModel
import com.dailystudio.devbricksx.compiler.utils.TypeNamesUtils
import com.dailystudio.devbricksx.compiler.utils.lowerCamelCaseName
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
class ViewModelProcessor : AbstractProcessor() {
    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(ViewModel::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        val viewModelGroups = mutableMapOf<String, MutableList<TypeElement>>()
        roundEnv.getElementsAnnotatedWith(ViewModel::class.java)
                .forEach {
                    if (it.kind != ElementKind.CLASS) {
                        processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Only classes can be annotated")
                        return true
                    }

                    if (it is TypeElement) {
                        mapElement(it, viewModelGroups)
                    }
                }


        for ((group, elements) in viewModelGroups.entries) {
            val result = processViewModelGroup(group, elements)

            result?.let {
                writeToFile(result)
            }
        }

        return true
    }

    private fun mapElement(element: TypeElement,
                           viewModelGroups: MutableMap<String, MutableList<TypeElement>>) {
        val typeName = element.simpleName.toString()

        val annotation = element.getAnnotation(ViewModel::class.java)
        var group = annotation.group

        if (group.isBlank()) {
            group = typeName
        }

        var elements = viewModelGroups[group]
        if (elements == null) {
            elements = mutableListOf()

            viewModelGroups[group] = elements
        }

        elements.add(element)
    }

    private fun processViewModelGroup(group: String,
                                      elements: List<TypeElement>): GeneratedResult? {
        if (elements.isEmpty()) {
            return null
        }

        val generatedClassName = GeneratedNames.getViewModelName(group)
        var packageName = processingEnv.elementUtils.getPackageOf(elements[0]).toString()
        if (elements.size == 1) {
            val annotation = elements[0].getAnnotation(ViewModel::class.java)
            if (annotation.packageName.isNotBlank()) {
                packageName = annotation.packageName
            }
        } else {
            elements.forEach {
                val annotation = it.getAnnotation(ViewModel::class.java)
                if (annotation.packageName.isNotBlank()) {
                    packageName = annotation.packageName
                }
            }
        }

        val classBuilder = TypeSpec.classBuilder(generatedClassName)
                .superclass(TypeNamesUtils.getAndroidViewModelTypeName())
                .addSuperclassConstructorParameter("application")
                .primaryConstructor(FunSpec.constructorBuilder()
                    .addParameter("application", TypeNamesUtils.getApplicationTypeName())
                    .build())

        elements.forEach{
            generateFacilitiesForElement(group, it, classBuilder)
        }

        return GeneratedResult(packageName, classBuilder)
    }

    private fun generateFacilitiesForElement(`group`: String,
                                             element: TypeElement,
                                             classBuilder: TypeSpec.Builder) {
        val typeName = element.simpleName.toString()
        var packageName = processingEnv.elementUtils.getPackageOf(element).toString()

        val repoName = GeneratedNames.getRepositoryName(typeName)
        val repoVariableName = repoName.lowerCamelCaseName()
        val repoPackageName = GeneratedNames.getRepositoryPackageName(packageName)
        val allName = GeneratedNames.getAllObjectPropertyName(typeName)
        val daoVariableName = GeneratedNames.getDaoVariableName(typeName)
        val databaseName = GeneratedNames.getDatabaseName(group.capitalize())
        val objectVariableName = GeneratedNames.getObjectVariableName(typeName)
        val objectsVariableName = GeneratedNames.getObjectsVariableName(typeName)

        val `object` = ClassName(packageName, typeName)
        val repo = ClassName(repoPackageName, repoName)
        val liveOfObjects = TypeNamesUtils.getListOfTypeName(`object`)
        val liveDataOfListOfObjects = TypeNamesUtils.getLiveDataOfListOfObjectName(`object`)
        val database = ClassName(packageName, databaseName)
        val dispatchers = TypeNamesUtils.getDispatchersTypeName()
        val viewModelScope = TypeNamesUtils.getViewModelScopeMemberName()
        val launch = TypeNamesUtils.getLaunchMemberName()
        val job = TypeNamesUtils.getJobTypeName()

        classBuilder.addProperty(repoVariableName, repo, KModifier.PRIVATE)
        classBuilder.addProperty(allName, liveDataOfListOfObjects)

        classBuilder.addInitializerBlock(CodeBlock.of(
                "   val %N = %T.getDatabase(application).%N()\n" +
                "   %N = %T(%N)\n" +
                "   %N = %N.%N\n",
                daoVariableName, database, daoVariableName,
                repoVariableName, repo, daoVariableName,
                allName, repoVariableName, allName
        ))

        val methodInsertOne = FunSpec.builder(GeneratedNames.getMethodName("insert", typeName))
                .addParameter(objectVariableName, `object`)
                .addStatement("return %M.%M(%T.IO) {\n" +
                        "   %N.insert(%N)\n" +
                        "}",
                        viewModelScope, launch, dispatchers,
                        repoVariableName, objectVariableName)
                .returns(job)
                .build()
        classBuilder.addFunction(methodInsertOne)

        val methodInsertAll = FunSpec.builder(GeneratedNames.getPluralMethodName("insert", typeName))
                .addParameter(objectsVariableName, liveOfObjects)
                .addStatement("return %M.%M(%T.IO) {\n" +
                        "   %N.insert(%N)\n" +
                        "}",
                        viewModelScope, launch, dispatchers,
                        repoVariableName, objectsVariableName)
                .returns(job)
                .build()
        classBuilder.addFunction(methodInsertAll)

        val methodUpdateOne = FunSpec.builder(GeneratedNames.getMethodName("update", typeName))
                .addParameter(objectVariableName, `object`)
                .addStatement("return %M.%M(%T.IO) {\n" +
                        "   %N.update(%N)\n" +
                        "}",
                        viewModelScope, launch, dispatchers,
                        repoVariableName, objectVariableName)
                .returns(job)
                .build()
        classBuilder.addFunction(methodUpdateOne)

        val methodUpdateAll = FunSpec.builder(GeneratedNames.getPluralMethodName("update", typeName))
                .addParameter(objectsVariableName, liveOfObjects)
                .addStatement("return %M.%M(%T.IO) {\n" +
                        "   %N.update(%N)\n" +
                        "}",
                        viewModelScope, launch, dispatchers,
                        repoVariableName, objectsVariableName)
                .returns(job)
                .build()
        classBuilder.addFunction(methodUpdateAll)

        val methodDeleteOne = FunSpec.builder(GeneratedNames.getMethodName("delete", typeName))
                .addParameter(objectVariableName, `object`)
                .addStatement("return %M.%M(%T.IO) {\n" +
                        "   %N.delete(%N)\n" +
                        "}",
                        viewModelScope, launch, dispatchers,
                        repoVariableName, objectVariableName)
                .returns(job)
                .build()
        classBuilder.addFunction(methodDeleteOne)
    }

    private fun writeToFile(result: GeneratedResult) {
        val typeSpec = result.classBuilder.build()

        typeSpec.name?.let { name ->
            val fileBuilder = FileSpec.builder(
                    result.packageName,
                    name)

            val file = fileBuilder.addType(typeSpec).build()

            val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
            file.writeTo(File(kaptKotlinGeneratedDir))
        }
    }

}