package com.dailystudio.devbricksx.compiler

import com.squareup.kotlinpoet.TypeSpec

data class GeneratedResult(val packageName: String,
                           val classBuilder: TypeSpec.Builder) {


}