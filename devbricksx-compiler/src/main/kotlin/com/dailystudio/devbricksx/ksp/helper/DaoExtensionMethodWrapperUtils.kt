package com.dailystudio.devbricksx.ksp.helper

import com.google.devtools.ksp.isConstructor
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toTypeName

object DaoExtensionMethodWrapperUtils {

    fun handleMethodsInDaoExtension(typeOfFuncCaller: ClassName,
                                    symbolOfDaoExtension: KSClassDeclaration,
                                    classBuilder: TypeSpec.Builder) {
        symbolOfDaoExtension.getAllFunctions().forEach {
            if (!validForWrap(it)) {
                return@forEach
            }

            wrapMethod(it, typeOfFuncCaller, classBuilder)
        }
    }

    private fun wrapMethod(func: KSFunctionDeclaration,
                           typeOfFuncCaller: ClassName,
                           classBuilder: TypeSpec.Builder) {
        val nameOfFunc = func.simpleName.getShortName()
        val nameOfPropOfFuncCaller: String =
            typeOfFuncCaller.simpleName.lowerCamelCaseName()
        val returnType = func.returnType?.toTypeName() ?: UNIT
        val hasReturn = (returnType != UNIT)

        val methodBuilder = FunSpec.builder(nameOfFunc)
            .addModifiers(KModifier.PUBLIC, KModifier.OPEN)
            .returns(returnType)

        val strOfFunCallBuilder = StringBuilder()

        for ((i, param) in func.parameters.withIndex()) {
            val nameOfParam = param.name?.getShortName()?: continue
            val typeOfParam = param.type.toTypeName()
            val isVararg = param.isVararg

            val paramBuilder = ParameterSpec.builder(nameOfParam, typeOfParam)
            if (isVararg) {
                paramBuilder.addModifiers(KModifier.VARARG)
            }

            if (param.isVararg) {
                strOfFunCallBuilder.append('*')
            }
            methodBuilder.addParameter(paramBuilder.build())

            strOfFunCallBuilder.append(nameOfParam)
            if (i < func.parameters.size - 1) {
                strOfFunCallBuilder.append(", ")
            }
        }

        if (hasReturn) {
            methodBuilder.addStatement(
                """
                    return %N.%N(%L)
                """.trimIndent(),
                nameOfPropOfFuncCaller,
                nameOfFunc,
                strOfFunCallBuilder.toString()
            )
        } else {
            methodBuilder.addStatement(
                """
                    %N.%N(%L)
                """.trimIndent(),
                nameOfPropOfFuncCaller,
                nameOfFunc,
                strOfFunCallBuilder.toString()
            )
        }

        classBuilder.addFunction(methodBuilder.build())
    }

    fun validForWrap(func: KSFunctionDeclaration): Boolean {
        return if (func.isConstructor()) {
            false
        } else {
            val nameOfFunc = func.simpleName.getShortName()
            val nameOfFuncToSkip = arrayOf(
                "equals",
                "hashCode",
                "toString",
            )

            !(nameOfFuncToSkip.contains(nameOfFunc))
        }
    }

}