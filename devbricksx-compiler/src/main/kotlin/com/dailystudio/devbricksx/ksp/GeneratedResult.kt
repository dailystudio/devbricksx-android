package com.dailystudio.devbricksx.ksp

import com.squareup.kotlinpoet.TypeSpec

data class GeneratedResult(val packageName: String,
                           val classBuilder: TypeSpec.Builder)