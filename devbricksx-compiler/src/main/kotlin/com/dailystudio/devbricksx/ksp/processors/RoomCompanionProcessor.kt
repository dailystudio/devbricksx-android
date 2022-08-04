package com.dailystudio.devbricksx.ksp.processors

import com.dailylstudio.devbricksx.annotations.plus.RoomCompanion
import com.dailystudio.devbricksx.ksp.GeneratedResult
import com.dailystudio.devbricksx.ksp.utils.asAnnotationType
import com.dailystudio.devbricksx.ksp.utils.getAnnotation
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName

class RoomCompanionProcessor(
    environment: SymbolProcessorEnvironment
) : BaseSymbolProcessor(environment) {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        warn("kotlin version: ${KotlinVersion.CURRENT}")

        val symbols = resolver
            .getSymbolsWithAnnotation(RoomCompanion::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
        symbols.forEach {
            warn("processing: ${it.toClassName()}")
            val result = genRoomCompanion(resolver, it)

            writeToFile(result)
        }

        return emptyList()
    }

    private fun genRoomCompanion(resolver: Resolver,
                                 clazz: KSClassDeclaration): GeneratedResult {
        warn("clazz: $clazz")

        val roomCompanion = clazz.getAnnotation(RoomCompanion::class, resolver)
        warn("roomCompanion: ${roomCompanion?.arguments}")

        return GeneratedResult(clazz.packageName.asString(),
            TypeSpec.classBuilder("_" + clazz.toClassName().simpleName))

    }
}