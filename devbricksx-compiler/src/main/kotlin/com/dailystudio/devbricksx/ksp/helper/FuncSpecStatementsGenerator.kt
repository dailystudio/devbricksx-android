package com.dailystudio.devbricksx.ksp.helper

import com.dailystudio.devbricksx.ksp.utils.TypeNameUtils
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.toTypeName

object FuncSpecStatementsGenerator {

    fun mapDefault(funcSpecBuilder: FunSpec.Builder,
                   hasReturn: Boolean,
                   nameOfWrappedFunc: String,
                   strOfParamsOfWrappedFunc: String? = null) {
        funcSpecBuilder.addStatement(
            if (hasReturn) {
                "return this.%N(%L)"
            } else {
                "this.%N(%L)"
            },
            nameOfWrappedFunc,
            strOfParamsOfWrappedFunc ?: ""
        )
    }

    fun mapOutputToObject(funcSpecBuilder: FunSpec.Builder,
                          returnType: TypeName,
                          nameOfWrappedFunc: String,
                          strOfParamsOfWrappedFunc: String? = null) {
        funcSpecBuilder.addStatement(
            "return this.%N(%L)%LtoObject()",
            nameOfWrappedFunc,
            strOfParamsOfWrappedFunc ?: "",
            if (returnType.isNullable) "?." else ".",
        )
    }

    private fun isReturnNullableObject(typeOfObject: TypeName,
                                       returnType: TypeName): Boolean {
        var returnNullable = false
        if (returnType is ParameterizedTypeName) {
            returnType.typeArguments.forEach {
                if (it is ParameterizedTypeName) {
                    returnNullable = isReturnNullableObject(typeOfObject, it)
                } else {
                    if (it.copy(nullable = false) == typeOfObject) {
                        returnNullable = it.isNullable
                    }
                }
            }
        }

        return returnNullable
    }

    fun mapOutputToObjects(funcSpecBuilder: FunSpec.Builder,
                           typeOfObject: TypeName,
                           returnType: TypeName,
                           nameOfWrappedFunc: String,
                           strOfParamsOfWrappedFunc: String? = null) {
        val returnNullable = isReturnNullableObject(typeOfObject, returnType)

        funcSpecBuilder
            .addStatement("""
                return this.%N(%L)%Lmap({ 
                    it%LtoObject() 
                })
            """.trimIndent(),
                nameOfWrappedFunc,
                strOfParamsOfWrappedFunc ?: "",
                if (returnType.isNullable) "?." else ".",
                if (returnNullable) "?." else ".",
            )
    }

    fun mapOutputToLiveDataOfObject(funcSpecBuilder: FunSpec.Builder,
                                    typeOfObject: TypeName,
                                    returnType: TypeName,
                                    nameOfWrappedFunc: String,
                                    strOfParamsOfWrappedFunc: String? = null) {
        val returnNullable = isReturnNullableObject(typeOfObject, returnType)

        funcSpecBuilder
            .addStatement(
                """
                    return this.%N(%L)%Llet({ livedata ->
                        %T.map(livedata, { 
                            it%LtoObject() 
                        })
                    })
                """.trimIndent(),
                nameOfWrappedFunc,
                strOfParamsOfWrappedFunc ?: "",
                if (returnType.isNullable) "?." else ".",
                TypeNameUtils.typeOfTransformations(),
                if (returnNullable) "?." else ".",
        )
    }

    fun mapOutputToLiveDataOfObjects(funcSpecBuilder: FunSpec.Builder,
                                     typeOfObject: TypeName,
                                     returnType: TypeName,
                                     nameOfWrappedFunc: String,
                                     strOfParamsOfWrappedFunc: String? = null) {
        val returnNullable = isReturnNullableObject(typeOfObject, returnType)

        funcSpecBuilder.addStatement(
            """
                return this.%N(%L)%Llet({ livedata ->
                    %T.map(livedata, {
                      mutableListOf<%T>().apply {
                        it.forEach {
                          add(it%LtoObject())
                        }
                      }
                    })
                })
            """.trimIndent(),
            nameOfWrappedFunc,
            strOfParamsOfWrappedFunc ?: "",
            if (returnType.isNullable) "?." else ".",
            TypeNameUtils.typeOfTransformations(),
            typeOfObject.copy(returnNullable),
            if (returnNullable) "?." else ".",
        )
    }

    fun mapOutputToFlowOfObject(funcSpecBuilder: FunSpec.Builder,
                                typeOfObject: TypeName,
                                returnType: TypeName,
                                nameOfWrappedFunc: String,
                                strOfParamsOfWrappedFunc: String? = null) {
        val returnNullable = isReturnNullableObject(typeOfObject, returnType)

        funcSpecBuilder.addStatement(
            """
                return this.%N(%L)%L%T({
                  it%LtoObject()
                })
            """.trimIndent(),
            nameOfWrappedFunc,
            strOfParamsOfWrappedFunc ?: "",
            if (returnType.isNullable) "?." else ".",
            TypeNameUtils.typeOfFlowMapFunction(),
            if (returnNullable) "?." else ".",
        )
    }

    fun mapOutputToFlowOfObjects(funcSpecBuilder: FunSpec.Builder,
                                 typeOfObject: TypeName,
                                 returnType: TypeName,
                                 nameOfWrappedFunc: String,
                                 strOfParamsOfWrappedFunc: String? = null) {
        val returnNullable = isReturnNullableObject(typeOfObject, returnType)

        funcSpecBuilder.addStatement(
            """
                return this.%N(%L)%L%T({
                  mutableListOf<%T>().apply {
                    it.forEach {
                      add(it%LtoObject())
                    }
                  }
                })
            """.trimIndent(),
            nameOfWrappedFunc,
            strOfParamsOfWrappedFunc ?: "",
            if (returnType.isNullable) "?." else ".",
            TypeNameUtils.typeOfFlowMapFunction(),
            typeOfObject.copy(returnNullable),
            if (returnNullable) "?." else ".",
        )
    }

    fun mapOutputToLiveDataOfPagedListObjects(funcSpecBuilder: FunSpec.Builder,
                                              returnType: TypeName,
                                              pageSize: Int,
                                              nameOfWrappedFunc: String,
                                              strOfParamsOfWrappedFunc: String? = null) {
        funcSpecBuilder.addStatement(
            """
                return this.%N(%L)%Llet({ livedata ->
                    %T(livedata.map({
                        it.toObject()
                    }), %L).build()
                })
            """.trimIndent(),
            nameOfWrappedFunc,
            strOfParamsOfWrappedFunc ?: "",
            if (returnType.isNullable) "?." else ".",
            TypeNameUtils.typeOfPagedListBuilder(),
            pageSize
        )
    }

    fun mapOutputToPagingSource(funcSpecBuilder: FunSpec.Builder,
                                returnType: TypeName,
                                nameOfWrappedFunc: String,
                                strOfParamsOfWrappedFunc: String? = null) {
        funcSpecBuilder.addStatement(
            """
                return this.%N(%L)%Lmap({
                    it.toObject()
                })%LasPagingSourceFactory()%Linvoke();
            """.trimIndent(),
            nameOfWrappedFunc,
            strOfParamsOfWrappedFunc ?: "",
            if (returnType.isNullable) "?." else ".",
            if (returnType.isNullable) "?." else ".",
            if (returnType.isNullable) "?." else ".",
        )
    }

    fun mapInputToCompanion(funcSpecBuilder: FunSpec.Builder,
                            typeOfObject: ClassName,
                            paramsToMap: Map<String, KSValueParameter>,
                            hasReturn: Boolean,
                            nameOfWrappedFunc: String,
                            strOfParamsOfWrappedFunc: String? = null,
    ) {
        val typeOfNullableObject = typeOfObject.copy(true)
        val typeOfCompanion = TypeNameUtils.typeOfCompanion(typeOfObject)
        val typeOfListOfObjects = TypeNameUtils.typeOfListOf(typeOfObject)
        val typeOfListOfNullableObjects = TypeNameUtils.typeOfListOf(typeOfObject.copy(true))

        for ((nameOfParam, param) in paramsToMap) {
            val typeOfParam = param.type.resolve().toTypeName()
            val mappedNameOfParam = FunctionNames.nameOfParamInWrappedFunc(nameOfParam)

            when (typeOfParam) {
                typeOfObject, typeOfNullableObject -> {
                    if (!param.isVararg) {
                        funcSpecBuilder.addStatement(
                            """
                                val %N = %T.fromObject(%N)
                            """.trimIndent(),
                            mappedNameOfParam,
                            typeOfCompanion,
                            nameOfParam,
                        )
                    } else {
                        funcSpecBuilder.addStatement(
                            """
                                val %N = %N.map({
                                    it%Llet({
                                        %T.fromObject(it)
                                    })
                                }).toTypedArray()
                            """.trimIndent(),
                            mappedNameOfParam,
                            nameOfParam,
                            if (typeOfParam == typeOfNullableObject) "?." else ".",
                            typeOfCompanion,
                        )
                    }
                }

                typeOfListOfObjects, typeOfListOfNullableObjects -> {
                    funcSpecBuilder.addStatement(
                        """
                            val %N = %N.map({ 
                                it%Llet({
                                    %T.fromObject(it)
                                 })
                            })
                        """.trimIndent(),
                        mappedNameOfParam,
                        nameOfParam,
                        if (typeOfParam == typeOfListOfNullableObjects) "?." else ".",
                        typeOfCompanion,
                    )
                }

                else -> {}
            }

        }

        if (hasReturn) {
            funcSpecBuilder.addStatement(
                """
                    return this.%N(%L)
                """.trimIndent(),
                nameOfWrappedFunc,
                strOfParamsOfWrappedFunc ?: "")
        } else {
            funcSpecBuilder.addStatement(
                """
                    this.%N(%L)
                """.trimIndent(),
                nameOfWrappedFunc,
                strOfParamsOfWrappedFunc ?: "")
        }
    }

}