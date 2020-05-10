package com.dailystudio.devbricksx.compiler.kotlin

import com.dailystudio.devbricksx.annotations.DaoExtension
import com.dailystudio.devbricksx.annotations.RoomCompanion
import com.dailystudio.devbricksx.annotations.ViewModel
import com.dailystudio.devbricksx.compiler.kotlin.utils.AnnotationsUtils
import com.dailystudio.devbricksx.compiler.kotlin.utils.TypeNamesUtils
import com.dailystudio.devbricksx.compiler.kotlin.utils.lowerCamelCaseName
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.*
import javax.tools.Diagnostic

@AutoService(Processor::class)
class ViewModelProcessor : BaseProcessor() {

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(ViewModel::class.java.name, DaoExtension::class.java.name)
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        val daoExtElements = mutableMapOf<ClassName, TypeElement>()
        roundEnv.getElementsAnnotatedWith(DaoExtension::class.java).forEach {
            if (it.kind != ElementKind.CLASS && it.kind != ElementKind.INTERFACE) {
                processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Only classes or interfaces can be annotated")
                return true
            }

            if (it is TypeElement) {
                val entityClassName = AnnotationsUtils.getClassValueFromAnnotation(it,
                "entity")
                if (entityClassName != null) {
                    daoExtElements[entityClassName] = it
                }
            }
        }
        println("daoExtElements: $daoExtElements")

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
            val result = processViewModelGroup(group, elements, daoExtElements)

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
                                      elements: List<TypeElement>,
                                      daoExtElements: Map<ClassName, TypeElement>): GeneratedResult? {
        if (elements.isEmpty()) {
            return null
        }

        val generatedClassName = GeneratedNames.getViewModelName(group)
        var packageName = processingEnv.elementUtils.getPackageOf(elements[0]).toString()

        val viewModelPackageName =
                GeneratedNames.getViewModelPackageName(packageName)
        println("group = $group, packageName = $packageName， viewModelPackage = $viewModelPackageName, generatedClassName = $generatedClassName")

        val classBuilder = TypeSpec.classBuilder(generatedClassName)
                .superclass(TypeNamesUtils.getAndroidViewModelTypeName())
                .addSuperclassConstructorParameter("application")
                .addModifiers(KModifier.OPEN)
                .primaryConstructor(FunSpec.constructorBuilder()
                    .addParameter("application", TypeNamesUtils.getApplicationTypeName())
                    .build())

        elements.forEach{
            generateFacilitiesForElement(it, classBuilder, daoExtElements)
        }

        return GeneratedResult(viewModelPackageName, classBuilder)
    }

    private fun generateFacilitiesForElement(element: TypeElement,
                                             classBuilder: TypeSpec.Builder,
                                             daoExtElements: Map<ClassName, TypeElement>) {
        val typeName = element.simpleName.toString()
        var packageName = processingEnv.elementUtils.getPackageOf(element).toString()
        println("typeName = $typeName, packageName = $packageName")

        val roomCompanion = element.getAnnotation(RoomCompanion::class.java)
        val databaseName = if (roomCompanion != null && roomCompanion.database.isNotBlank()) {
            GeneratedNames.getDatabaseName(roomCompanion.database)
        } else {
            GeneratedNames.getDatabaseName(typeName)
        }

        val repoName = GeneratedNames.getRepositoryName(typeName)
        val repoVariableName = repoName.lowerCamelCaseName()
        val repoPackageName = GeneratedNames.getRepositoryPackageName(packageName)
        val allName = GeneratedNames.getAllObjectsPropertyName(typeName)
        val allPagedName = GeneratedNames.getAllObjectsPagedPropertyName(typeName)
        val daoVariableName = GeneratedNames.getDaoVariableName(typeName)
        val objectVariableName = GeneratedNames.getObjectVariableName(typeName)
        val objectsVariableName = GeneratedNames.getObjectsVariableName(typeName)

        val `object` = ClassName(packageName, typeName)
        val repo = ClassName(repoPackageName, repoName)
        val listOfObjects = TypeNamesUtils.getListOfTypeName(`object`)
        val liveDataOfListOfObjects = TypeNamesUtils.getLiveDataOfListOfObjectTypeName(`object`)
        val liveDataOfPagedListOfObjects = TypeNamesUtils.getLiveDataOfPagedListOfObjectsTypeName(`object`)
        val database = ClassName(packageName, databaseName)
        val dispatchers = TypeNamesUtils.getDispatchersTypeName()
        val viewModelScope = TypeNamesUtils.getViewModelScopeMemberName()
        val launch = TypeNamesUtils.getLaunchMemberName()
        val job = TypeNamesUtils.getJobTypeName()

        classBuilder.addProperty(repoVariableName, repo, KModifier.PROTECTED)
        classBuilder.addProperty(allName, liveDataOfListOfObjects)
        classBuilder.addProperty(allPagedName, liveDataOfPagedListOfObjects)

        classBuilder.addInitializerBlock(CodeBlock.of(
                "   val %N = %T.getDatabase(application).%N()\n" +
                "   %N = %T(%N)\n" +
                "   %N = %N.%N\n" +
                "   %N = %N.%N\n",
                daoVariableName, database, daoVariableName,
                repoVariableName, repo, daoVariableName,
                allName, repoVariableName, allName,
                allPagedName, repoVariableName, allPagedName
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
                .addParameter(objectsVariableName, listOfObjects)
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
                .addParameter(objectsVariableName, listOfObjects)
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

        val daoExtElement = daoExtElements[`object`] ?: return

        val subElements: List<Element> = daoExtElement.enclosedElements

        var executableElement: ExecutableElement
        for (subElement in subElements) {
            if (subElement is ExecutableElement) {
                println("processing method: $subElement")
                if (Constants.CONSTRUCTOR_NAME == subElement.getSimpleName().toString()) {
                    println("constructor method, skip")
                    continue
                }

                executableElement = subElement
                handleExtendedMethod(`object`,
                        repoVariableName, executableElement, classBuilder)
            }
        }
    }

    private fun handleExtendedMethod(objectTypeName: TypeName,
                                     repoVariableName: String,
                                     executableElement: ExecutableElement,
                                     classBuilder: TypeSpec.Builder) {
//        val methodName = executableElement.simpleName.toString().kotlinGetterName()
        val methodName = executableElement.simpleName.toString()
        var returnTypeName = javaToKotlinTypeName(objectTypeName, executableElement.returnType.asTypeName())
        println("returnTypeName = $returnTypeName")

        val hasReturn: Boolean = !TypeNamesUtils.isTypeNameUnit(returnTypeName)

        val methodSpecBuilder = FunSpec.builder(methodName)
                .addModifiers(KModifier.PUBLIC)
                .returns(returnTypeName)

        var parameters: List<VariableElement> =
                executableElement.parameters

        val parametersBuilder = StringBuilder()

        var paramName: String
        var param: VariableElement
        for (i in parameters.indices) {
            param = parameters[i]
            paramName = param.simpleName.toString()

            methodSpecBuilder.addParameter(paramName,
                    javaToKotlinTypeName(objectTypeName,
                            param.asType().asTypeName()))

            parametersBuilder.append(paramName)
            if (i < parameters.size - 1) {
                parametersBuilder.append(", ")
            }
        }

        val methodParameters = parametersBuilder.toString()

        if (hasReturn) {
            if (methodParameters.isNotEmpty()) {
                methodSpecBuilder.addStatement("return %N.%N(%L)",
                        repoVariableName, methodName, methodParameters)
            } else {
                methodSpecBuilder.addStatement("return %N.%N()",
                        repoVariableName, methodName)
            }
        } else {
            if (methodParameters.isNotEmpty()) {
                methodSpecBuilder.addStatement("%N.%N(%L)",
                        repoVariableName, methodName, methodParameters)
            } else {
                methodSpecBuilder.addStatement("%N.%N()",
                        repoVariableName, methodName)
            }
        }

        classBuilder.addFunction(methodSpecBuilder.build())
    }

    private fun javaToKotlinTypeName(objectTypeName: TypeName,
                                     origTypeName: TypeName): TypeName {
        val array = TypeNamesUtils.getArrayTypeName(objectTypeName)
        val javaListOfObjects = TypeNamesUtils.getJavaListOfTypeName(objectTypeName)
        val javaLong = TypeNamesUtils.getJavaLongTypeName()
        val javaString = TypeNamesUtils.getJavaStringTypeName()

        println("origTypeName = $origTypeName, " +
                "object = $objectTypeName, " +
                "array = $array, " +
                "javaListOfObjects = $javaListOfObjects, " +
                "javaLong = $javaLong" +
                "javaString = $javaString"
        )

        when (origTypeName) {
            is ParameterizedTypeName -> {
                val liveDataTypeName = TypeNamesUtils.getLiveDataTypeName()
                val javaListTypeName = TypeNamesUtils.getJavaListTypeName()
                val listTypeName = TypeNamesUtils.getListTypeName()

                println("rawType = ${origTypeName.rawType}")
                println("typeArguments = ${origTypeName.typeArguments}")

                val rawType = origTypeName.rawType
                val typeArguments = origTypeName.typeArguments

                when (rawType) {
                    javaListTypeName -> {
                        if (typeArguments.isNotEmpty()) {
                            val newTypeName = javaToKotlinTypeName(objectTypeName,
                                    typeArguments[0])

                            return listTypeName.parameterizedBy(newTypeName)
                        }
                    }

                    liveDataTypeName -> {
                        if (typeArguments.isNotEmpty()) {
                            val newTypeName = javaToKotlinTypeName(objectTypeName,
                                    typeArguments[0])

                            return liveDataTypeName.parameterizedBy(newTypeName)
                        }
                    }

                    array -> {
                        if (typeArguments.isNotEmpty()) {
                            val ta0Name = typeArguments[0].toString()
                            println("ta0Name = $ta0Name")
                            when (ta0Name) {
                                "kotlin.Int" -> {
                                    val primitiveType = ta0Name.removePrefix("kotlin")
                                    return ClassName("kotlin", "${primitiveType}Array")
                                }
                            }
                        }
                    }
                }
            }

            javaLong -> {
                return TypeNamesUtils.getLongTypeName()
            }

            javaString -> {
                return TypeNamesUtils.getStringTypeName()
            }
        }

        return origTypeName
    }

}