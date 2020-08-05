package com.dailystudio.devbricksx.compiler.kotlin

import com.dailystudio.devbricksx.annotations.PreferenceValue
import com.dailystudio.devbricksx.annotations.SharedPreference
import com.dailystudio.devbricksx.compiler.kotlin.utils.TypeNamesUtils
import com.dailystudio.devbricksx.compiler.kotlin.utils.kebabCaseName
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import org.jetbrains.annotations.Nullable
import java.lang.NumberFormatException
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.*
import javax.lang.model.type.TypeMirror


@AutoService(Processor::class)
class SharedPreferenceProcessor : BaseProcessor() {

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(SharedPreference::class.java.name)
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(SharedPreference::class.java)
                .forEach { element ->
                    if (element.kind != ElementKind.CLASS) {
                        error("Only classes can be annotated")
                        return true
                    }

                    if (element is TypeElement) {
                       val result = generateSharedPreference(element)

                        result?.let {
                            writeToFile(it)
                        }
                    }
                }


        return true
    }

    private fun generateSharedPreference(element: TypeElement) : GeneratedResult? {
        val typeName = element.simpleName.toString()

        var packageName = processingEnv.elementUtils.getPackageOf(element).toString()

        val generatedClassName = GeneratedNames.getSharedPrefsName(typeName)
        val objectTypeName = ClassName(packageName, typeName)
        val prefName = typeName.kebabCaseName()
        val absPrefsTypeName = TypeNamesUtils.getAbsPrefsTypeName()
        val globalContextWrapperTypeName = TypeNamesUtils.getGlobalContextWrapperTypeName()
        val booleanTypeName = TypeNamesUtils.getBooleanTypeName()
        val floatTypeName = TypeNamesUtils.getFloatTypeName()
        val longTypeName = TypeNamesUtils.getLongTypeName()
        val integerTypeName = TypeNamesUtils.getIntegerTypeName()
        val stringTypeName = TypeNamesUtils.getStringTypeName()

        val classBuilder = TypeSpec.objectBuilder(generatedClassName)
                .superclass(absPrefsTypeName)

        val prefNameFieldBuilder = PropertySpec.builder("prefName", String::class)
//                .addModifiers(KModifier.PROTECTED)
                .addModifiers(KModifier.OVERRIDE)
                .initializer("%S", prefName)

        val subElements: List<Element?> = element.enclosedElements

        for (subElement in subElements) {
            if (subElement is VariableElement) {
                val defValAnnotation =
                        subElement.getAnnotation(PreferenceValue::class.java) ?: continue

                val defValStr = defValAnnotation.defaultValueStr

                debug("processing field: %s[%s], default value: [str = %s]",
                        subElement, subElement.asType().asTypeName(), defValStr)
                val varName: String = subElement.getSimpleName().toString()
                val keyName: String = varName.kebabCaseName()
                val prefKeyName: String = GeneratedNames.getPreferenceKeyName(varName)
                val fieldType: TypeMirror = subElement.asType()

                val prefNameConstant = PropertySpec.builder(prefKeyName, String::class)
                        .addModifiers(KModifier.CONST)
                        .initializer("%S", keyName)

                classBuilder.addProperty(prefNameConstant.build())

                val nullable = (subElement.getAnnotation(Nullable::class.java) != null)

                val propBuilder = PropertySpec.builder(varName,
                        TypeNamesUtils.javaToKotlinTypeName(fieldType.asTypeName()).copy(nullable = nullable))
                        .mutable()

                val getterBuilder = FunSpec.getterBuilder()
                val setterBuilder = FunSpec.setterBuilder()
                val initializerBuilder = CodeBlock.builder()

                when (TypeNamesUtils.javaToKotlinTypeName(fieldType.asTypeName())) {
                    booleanTypeName -> {
                        val defaultVal: Boolean = defValStr.toBoolean()

                        getterBuilder.addStatement("val defaultVal = %L",
                                defaultVal)
                        getterBuilder.addStatement("val context = %L.context ?: return defaultVal",
                                globalContextWrapperTypeName)
                        getterBuilder.addStatement("return getBooleanPrefValue(context, %S, defaultVal)",
                                keyName)

                        setterBuilder.addParameter("value", booleanTypeName)
                        setterBuilder.addStatement("field = value")
                        setterBuilder.addStatement("val context = %L.context ?: return",
                                globalContextWrapperTypeName)
                        setterBuilder.addStatement("setBooleanPrefValue(context, %S, value)",
                                keyName)

                        initializerBuilder.addStatement("%L", defaultVal)
                    }

                    integerTypeName -> {
                        val defaultVal: Int = try {
                            defValStr.toInt()
                        } catch (e: NumberFormatException) {
                            error("failed to parse default int value from [%s]: %s",
                                    defValStr, e)

                            0
                        }
                        getterBuilder.addStatement("val defaultVal = %L.toInt()",
                                defaultVal)
                        getterBuilder.addStatement("val context = %L.context ?: return defaultVal",
                                globalContextWrapperTypeName)
                        getterBuilder.addStatement("return getIntegerPrefValue(context, %S, defaultVal)",
                                keyName)

                        setterBuilder.addParameter("value", floatTypeName)
                        setterBuilder.addStatement("field = value")
                        setterBuilder.addStatement("val context = %L.context ?: return",
                                globalContextWrapperTypeName)
                        setterBuilder.addStatement("setIntegerPrefValue(context, %S, value)",
                                keyName)

                        initializerBuilder.addStatement("%L.toInt()", defaultVal)
                    }

                    floatTypeName -> {
                        val defaultVal: Float = try {
                            defValStr.toFloat()
                        } catch (e: NumberFormatException) {
                            error("failed to parse default float value from [%s]: %s",
                                    defValStr, e)

                            0f
                        }
                        getterBuilder.addStatement("val defaultVal = %L.toFloat()",
                                defaultVal)
                        getterBuilder.addStatement("val context = %L.context ?: return defaultVal",
                                globalContextWrapperTypeName)
                        getterBuilder.addStatement("return getFloatPrefValue(context, %S, defaultVal)",
                                keyName)

                        setterBuilder.addParameter("value", floatTypeName)
                        setterBuilder.addStatement("field = value")
                        setterBuilder.addStatement("val context = %L.context ?: return",
                                globalContextWrapperTypeName)
                        setterBuilder.addStatement("setFloatPrefValue(context, %S, value)",
                                keyName)

                        initializerBuilder.addStatement("%L.toFloat()", defaultVal)
                    }

                    stringTypeName -> {
                        val defaultVal: String = defValStr
                        getterBuilder.addStatement("val defaultVal = %S",
                                defaultVal)
                        getterBuilder.addStatement("val context = %L.context ?: return defaultVal",
                                globalContextWrapperTypeName)
                        getterBuilder.addStatement("return getStringPrefValue(context, %S) ?: defaultVal",
                                keyName)

                        setterBuilder.addParameter("value", stringTypeName)
                        setterBuilder.addStatement("field = value")
                        setterBuilder.addStatement("val context = %L.context ?: return",
                                globalContextWrapperTypeName)
                        setterBuilder.addStatement("setStringPrefValue(context, %S, value)",
                                keyName)

                        initializerBuilder.addStatement("%S", defaultVal)
                    }
                }

                propBuilder.getter(getterBuilder.build())
                propBuilder.setter(setterBuilder.build())
                propBuilder.initializer(initializerBuilder.build())

                classBuilder.addProperty(propBuilder.build())
            }
        }

        classBuilder.addProperty(prefNameFieldBuilder.build())

        return GeneratedResult(
                packageName,
                classBuilder)
    }

}