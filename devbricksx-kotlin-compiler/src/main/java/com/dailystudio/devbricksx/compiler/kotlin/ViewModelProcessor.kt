package com.dailystudio.devbricksx.compiler.kotlin

import com.dailystudio.devbricksx.annotations.DaoExtension
import com.dailystudio.devbricksx.annotations.InMemoryRepository
import com.dailystudio.devbricksx.annotations.RoomCompanion
import com.dailystudio.devbricksx.annotations.ViewModel
import com.dailystudio.devbricksx.compiler.kotlin.utils.*
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.*

@AutoService(Processor::class)
class ViewModelProcessor : BaseProcessor() {

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(ViewModel::class.java.name, DaoExtension::class.java.name)
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        val daoExtElements = mutableMapOf<ClassName, TypeElement>()
        roundEnv.getElementsAnnotatedWith(DaoExtension::class.java).forEach {
            if (it.kind != ElementKind.CLASS && it.kind != ElementKind.INTERFACE) {
                error("Only classes or interfaces can be annotated")
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
        debug("daoExtElements: $daoExtElements")

        val viewModelGroups = mutableMapOf<String, MutableList<TypeElement>>()
        roundEnv.getElementsAnnotatedWith(ViewModel::class.java)
                .forEach {
                    if (it.kind != ElementKind.CLASS) {
                        error("Only classes can be annotated")
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
        val typeName = element.qualifiedName.toString()

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

        var typeName = if (group.contains(".")) {
             group.split(".").last()
        } else {
            group
        }

        val generatedClassName = GeneratedNames.getViewModelName(typeName)
        var packageName = processingEnv.elementUtils.getPackageOf(elements[0]).toString()

        val viewModelPackageName =
                GeneratedNames.getViewModelPackageName(packageName)
        debug("group = $group, packageName = $packageName， viewModelPackage = $viewModelPackageName, generatedClassName = $generatedClassName")

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
        debug("typeName = $typeName, packageName = $packageName")

        val roomCompanion = element.getAnnotation(RoomCompanion::class.java)
        val databaseName = if (roomCompanion != null && roomCompanion.database.isNotBlank()) {
            GeneratedNames.getDatabaseName(roomCompanion.database)
        } else {
            GeneratedNames.getDatabaseName(typeName)
        }

        val inMemoryRepository = (element.getAnnotation(InMemoryRepository::class.java) != null)

        val repoName = GeneratedNames.getRepositoryName(typeName)
        val repoVariableName = repoName.lowerCamelCaseName()
        val repoPackageName = GeneratedNames.getRepositoryPackageName(packageName)
        val allName = GeneratedNames.getAllObjectsLivePropertyName(typeName)
        val allPagedName = GeneratedNames.getAllObjectsPagedPropertyName(typeName)
        val allFlowName = GeneratedNames.getAllObjectsFlowPropertyName(typeName)
        val repoAllName = GeneratedNames.getAllObjectsLivePropertyName(
                if (inMemoryRepository) "Object" else typeName)
        val repoAllPagedName = GeneratedNames.getAllObjectsPagedPropertyName(
                if (inMemoryRepository) "Object" else typeName)
        val repoAllFlowName = GeneratedNames.getAllObjectsFlowPropertyName(
                if (inMemoryRepository) "Object" else typeName)
        val daoVariableName = GeneratedNames.getDaoVariableName(typeName)
        val objectVariableName = GeneratedNames.getObjectVariableName(typeName)
        val objectsVariableName = GeneratedNames.getObjectsVariableName(typeName)

        val `object` = ClassName(packageName, typeName)
        val repo = ClassName(repoPackageName, repoName)
        val listOfObjects = TypeNamesUtils.getListOfTypeName(`object`)
        val liveDataOfListOfObjects = TypeNamesUtils.getLiveDataOfListOfObjectTypeName(`object`)
        val liveDataOfPagedListOfObjects = TypeNamesUtils.getLiveDataOfPagedListOfObjectsTypeName(`object`)
        val flowOfListOfObjects = TypeNamesUtils.getFlowOfListOfObjectTypeName(`object`)
        val pagingSourceOfObjects = TypeNamesUtils.getPagingSourceOfTypeName(`object`)
        val database = ClassName(packageName, databaseName)
        val dispatchers = TypeNamesUtils.getDispatchersTypeName()
        val launch = TypeNamesUtils.getLaunchMemberName()
        val job = TypeNamesUtils.getJobTypeName()
        val shareIn = TypeNamesUtils.getShareInTypeName()
        val sharingStarted = TypeNamesUtils.getSharingStartedTypeName()
        val viewModelScope = TypeNamesUtils.getViewModelScopeTypeName()

        val primaryKeyFields = mutableMapOf<String, TypeName>()
        if (roomCompanion != null) {
            FieldsUtils.collectPrimaryKeyFields(element,
                    `object`,
                    primaryKeyFields,
                    processingEnv.typeUtils)

            debug("collected primaryKeyFields = $primaryKeyFields")
        }

        val getOneCallParameters = FieldsUtils.primaryKeyFieldsToFuncCallParameters(
                primaryKeyFields)

        classBuilder.addProperty(repoVariableName, repo, KModifier.PROTECTED)
        classBuilder.addProperty(allName, liveDataOfListOfObjects)
        classBuilder.addProperty(allPagedName, liveDataOfPagedListOfObjects)
        classBuilder.addProperty(allFlowName, flowOfListOfObjects)

        if (inMemoryRepository) {
            classBuilder.addInitializerBlock(CodeBlock.of(
                    "   %N = %T()\n" +
                    "   %N = %N.%N\n" +
                    "   %N = %N.%N\n" +
                    "   %N = %N.%N.%T(%T, %T.Eagerly)\n",
                    repoVariableName, repo,
                    allName, repoVariableName, repoAllName,
                    allPagedName, repoVariableName, repoAllPagedName,
                    allFlowName, repoVariableName, repoAllFlowName, shareIn, viewModelScope, sharingStarted
            ))
        } else {
            classBuilder.addInitializerBlock(CodeBlock.of(
                    "   val %N = %T.getDatabase(application).%N()\n" +
                    "   %N = %T(%N)\n" +
                    "   %N = %N.%N\n" +
                    "   %N = %N.%N\n" +
                    "   %N = %N.%N.%T(%T, %T.Eagerly)\n",
                    daoVariableName, database, daoVariableName,
                    repoVariableName, repo, daoVariableName,
                    allName, repoVariableName, repoAllName,
                    allPagedName, repoVariableName, repoAllPagedName,
                    allFlowName, repoVariableName, repoAllFlowName, shareIn, viewModelScope, sharingStarted
            ))
        }

        val methodGetAllPagingSourceBuilder = FunSpec.builder(
                GeneratedNames.getAllObjectsPagingSourceFunName(typeName))
                .returns(pagingSourceOfObjects)
        methodGetAllPagingSourceBuilder.addStatement("return %N.%N",
                repoVariableName,
                GeneratedNames.getAllObjectsPagingSourcePropertyName(if (inMemoryRepository) {
                    "Object"
                } else {
                    typeName
                }))

        classBuilder.addFunction(methodGetAllPagingSourceBuilder.build())

        val methodGetOneBuilder = FunSpec.builder(GeneratedNames.getMethodName("get", typeName))
                .returns(`object`.copy(nullable = true))

        if (!inMemoryRepository) {
            methodGetOneBuilder.addModifiers(KModifier.SUSPEND)

            FieldsUtils.primaryKeyFieldsToMethodParameters(methodGetOneBuilder, primaryKeyFields)

            methodGetOneBuilder.addStatement("return %N.%N(%L)",
                    repoVariableName,
                    GeneratedNames.getMethodName("get", typeName),
                    getOneCallParameters)
        } else {
            val keyClass = AnnotationsUtils.getClassValueFromAnnotation(element, "key") ?: return

            methodGetOneBuilder.addParameter("key",
                    KotlinTypesUtils.javaToKotlinTypeName(`object`, keyClass))
            methodGetOneBuilder.addStatement("return %N.get(key)",
                    repoVariableName)
        }

        classBuilder.addFunction(methodGetOneBuilder.build())

        val methodGetAllBuilder = FunSpec.builder(GeneratedNames.getAllObjectsMethodName(typeName))
                .returns(listOfObjects)

        if (!inMemoryRepository) {
            methodGetAllBuilder.addModifiers(KModifier.SUSPEND)
        }

        methodGetAllBuilder.addStatement("return %N.%N()",
                repoVariableName,
                GeneratedNames.getAllObjectsRepoMethodName(if (inMemoryRepository) "Object" else typeName))

        classBuilder.addFunction(methodGetAllBuilder.build())

        val methodInsertOne = FunSpec.builder(GeneratedNames.getMethodName("insert", typeName))
                .addParameter(objectVariableName, `object`)
                .addStatement("return %T.%M(%T.IO) {\n" +
                        "   %N.insert(%N)\n" +
                        "}",
                        viewModelScope, launch, dispatchers,
                        repoVariableName, objectVariableName)
                .returns(job)
                .build()
        classBuilder.addFunction(methodInsertOne)

        val methodInsertAll = FunSpec.builder(GeneratedNames.getPluralMethodName("insert", typeName))
                .addParameter(objectsVariableName, listOfObjects)
                .addStatement("return %T.%M(%T.IO) {\n" +
                        "   %N.insert(%N)\n" +
                        "}",
                        viewModelScope, launch, dispatchers,
                        repoVariableName, objectsVariableName)
                .returns(job)
                .build()
        classBuilder.addFunction(methodInsertAll)

        val methodUpdateOne = FunSpec.builder(GeneratedNames.getMethodName("update", typeName))
                .addParameter(objectVariableName, `object`)
                .addStatement("return %T.%M(%T.IO) {\n" +
                        "   %N.update(%N)\n" +
                        "}",
                        viewModelScope, launch, dispatchers,
                        repoVariableName, objectVariableName)
                .returns(job)
                .build()
        classBuilder.addFunction(methodUpdateOne)

        val methodUpdateAll = FunSpec.builder(GeneratedNames.getPluralMethodName("update", typeName))
                .addParameter(objectsVariableName, listOfObjects)
                .addStatement("return %T.%M(%T.IO) {\n" +
                        "   %N.update(%N)\n" +
                        "}",
                        viewModelScope, launch, dispatchers,
                        repoVariableName, objectsVariableName)
                .returns(job)
                .build()
        classBuilder.addFunction(methodUpdateAll)

        val methodInsertOrUpdateOne = FunSpec.builder(GeneratedNames.getMethodName("insertOrUpdate", typeName))
                .addParameter(objectVariableName, `object`)
                .addStatement("return %T.%M(%T.IO) {\n" +
                        "   %N.insertOrUpdate(%N)\n" +
                        "}",
                        viewModelScope, launch, dispatchers,
                        repoVariableName, objectVariableName)
                .returns(job)
                .build()
        classBuilder.addFunction(methodInsertOrUpdateOne)

        val methodInsertOrUpdateAll = FunSpec.builder(GeneratedNames.getPluralMethodName("insertOrUpdate", typeName))
                .addParameter(objectsVariableName, listOfObjects)
                .addStatement("return %T.%M(%T.IO) {\n" +
                        "   %N.update(%N)\n" +
                        "}",
                        viewModelScope, launch, dispatchers,
                        repoVariableName, objectsVariableName)
                .returns(job)
                .build()
        classBuilder.addFunction(methodInsertOrUpdateAll)

        val methodDeleteOne = FunSpec.builder(GeneratedNames.getMethodName("delete", typeName))
                .addParameter(objectVariableName, `object`)
                .addStatement("return %T.%M(%T.IO) {\n" +
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
                debug("processing method: $subElement")
                if (Constants.CONSTRUCTOR_NAME == subElement.getSimpleName().toString()) {
                    debug("constructor method, skip")
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
        val returnTypeName = KotlinTypesUtils.javaToKotlinTypeName(objectTypeName, executableElement.returnType.asTypeName())
        debug("returnTypeName = $returnTypeName")

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
                    KotlinTypesUtils.javaToKotlinTypeName(objectTypeName,
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

}