package com.dailystudio.devbricksx.ksp.helper

import com.dailystudio.devbricksx.ksp.utils.TypeNameUtils
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
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
                          nameOfWrappedFunc: String,
                          strOfParamsOfWrappedFunc: String? = null) {
        funcSpecBuilder.addStatement(
            "return this.%N(%L).toObject()",
            nameOfWrappedFunc,
            strOfParamsOfWrappedFunc ?: ""
        )
    }

    fun mapOutputToLiveDataOfObject(funcSpecBuilder: FunSpec.Builder,
                                    nameOfWrappedFunc: String,
                                    strOfParamsOfWrappedFunc: String? = null) {
        funcSpecBuilder
            .addStatement(
                """
                    return %T.map(this.%N(%L)) { 
                        it.toObject() 
                    }
                """.trimIndent(),
                TypeNameUtils.typeOfTransformations(),
                nameOfWrappedFunc,
                strOfParamsOfWrappedFunc ?: "",
        )
    }

    fun mapOutputToObjects(funcSpecBuilder: FunSpec.Builder,
                           nameOfWrappedFunc: String,
                           strOfParamsOfWrappedFunc: String? = null) {
        funcSpecBuilder
            .addStatement("""
                return this.%N(%L).map({ 
                    it.toObject() 
                })
            """.trimIndent(),
            nameOfWrappedFunc,
            strOfParamsOfWrappedFunc ?: "",
        )
    }

    fun mapOutputToLiveDataOfObjects(funcSpecBuilder: FunSpec.Builder,
                                     typeOfObject: TypeName,
                                     nameOfWrappedFunc: String,
                                     strOfParamsOfWrappedFunc: String? = null) {
        funcSpecBuilder.addStatement(
            """
                return %T.map(this.%N(%L)) {
                  mutableListOf<%T>().apply {
                    it.forEach {
                      add(it.toObject())
                    }
                  }
                }
            """.trimIndent(),
            TypeNameUtils.typeOfTransformations(),
            nameOfWrappedFunc,
            strOfParamsOfWrappedFunc ?: "",
            typeOfObject
        )
    }

    fun mapOutputToFlowOfObjects(funcSpecBuilder: FunSpec.Builder,
                                 typeOfObject: TypeName,
                                 nameOfWrappedFunc: String,
                                 strOfParamsOfWrappedFunc: String? = null) {
        funcSpecBuilder.addStatement(
            """
                return this.%N(%L).%T {
                  mutableListOf<%T>().apply {
                    it.forEach {
                      add(it.toObject())
                    }
                  }
                }
            """.trimIndent(),
            nameOfWrappedFunc,
            strOfParamsOfWrappedFunc ?: "",
            TypeNameUtils.typeOfFlowMapFunction(),
            typeOfObject
        )
    }

    fun mapOutputToLiveDataOfPagedListObjects(funcSpecBuilder: FunSpec.Builder,
                                              pageSize: Int,
                                              nameOfWrappedFunc: String,
                                              strOfParamsOfWrappedFunc: String? = null) {
        funcSpecBuilder.addStatement(
            """
                return %T(this.%N(%L).map({
                    it.toObject()
                }), %L).build()
            """.trimIndent(),
            TypeNameUtils.typeOfPagedListBuilder(),
            nameOfWrappedFunc,
            strOfParamsOfWrappedFunc ?: "",
            pageSize
        )
    }

    fun mapOutputToPagingSource(funcSpecBuilder: FunSpec.Builder,
                                nameOfWrappedFunc: String,
                                strOfParamsOfWrappedFunc: String? = null) {
        funcSpecBuilder.addStatement(
            """
                return this.%N(%L).map({
                    it.toObject()
                }).asPagingSourceFactory().invoke();
            """.trimIndent(),
            nameOfWrappedFunc,
            strOfParamsOfWrappedFunc ?: "",
        )
    }

    fun mapInputToCompanion(funcSpecBuilder: FunSpec.Builder,
                            typeOfObject: ClassName,
                            paramsToMap: Map<String, KSValueParameter>,
                            hasReturn: Boolean,
                            nameOfWrappedFunc: String,
                            strOfParamsOfWrappedFunc: String? = null,
    ) {
        val typeOfCompanion = TypeNameUtils.typeOfCompanion(typeOfObject)
        val typeOfListOfObjects = TypeNameUtils.typeOfListOf(typeOfObject)
        val typeOfListOfCompanions = TypeNameUtils.typeOfListOf(typeOfCompanion)
        val typeOfArrayOfObjects = TypeNameUtils.typeOfArrayOf(typeOfObject)
        val typeOfArrayOfCompanions = TypeNameUtils.typeOfArrayOf(typeOfCompanion)

        for ((nameOfParam, param) in paramsToMap) {
            val typeOfParam = param.type.resolve().toTypeName()
            val mappedNameOfParam = GeneratedNames
                .mappedNameOfParamInWrappedFunc(nameOfParam)

            when (typeOfParam) {
                typeOfObject -> {
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
                                val %N = %N.map({ %T.fromObject(it) }).toTypedArray()
                            """.trimIndent(),
                            mappedNameOfParam,
                            nameOfParam,
                            typeOfCompanion,
                        )
                    }
                }

                typeOfListOfObjects -> {
                    funcSpecBuilder.addStatement(
                        """
                            val %N = %N.map({ %T.fromObject(it) })
                        """.trimIndent(),
                        mappedNameOfParam,
                        nameOfParam,
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