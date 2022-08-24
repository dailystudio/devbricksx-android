package com.dailystudio.devbricksx.ksp.helper

import com.dailystudio.devbricksx.ksp.utils.TypeNamesUtils
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeName

object FuncSpecStatementsGenerator {

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
            .addStatement("""
                return this.%N(%L).%T { 
                    it.toObject() 
                }
            """.trimIndent(),
            nameOfWrappedFunc,
            strOfParamsOfWrappedFunc ?: "",
            TypeNamesUtils.typeOfLiveDataMapFunction()
        )
    }

    fun mapOutputToObjects(funcSpecBuilder: FunSpec.Builder,
                           nameOfWrappedFunc: String,
                           strOfParamsOfWrappedFunc: String? = null) {
        funcSpecBuilder
            .addStatement("""
                return this.%N(%L).map { 
                    it.toObject() 
                }
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
            TypeNamesUtils.typeOfTransformations(),
            nameOfWrappedFunc,
            strOfParamsOfWrappedFunc ?: "",
            typeOfObject
        )
    }

}