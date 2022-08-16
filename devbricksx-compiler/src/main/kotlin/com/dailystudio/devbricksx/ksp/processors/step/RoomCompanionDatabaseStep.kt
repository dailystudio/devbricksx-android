package com.dailystudio.devbricksx.ksp.processors.step

import androidx.room.Entity
import com.dailystudio.devbricksx.ksp.GeneratedResult
import com.dailystudio.devbricksx.ksp.ProcessStep
import com.dailystudio.devbricksx.ksp.processors.BaseSymbolProcessor
import com.dailystudio.devbricksx.ksp.utils.*
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration

class RoomCompanionDatabaseStep(processor: BaseSymbolProcessor)
    : ProcessStep(Entity::class.qualifiedName!!, processor) {

    override fun process(resolver: Resolver, clazz: KSClassDeclaration): GeneratedResult? {
        val typeName = clazz.typeName()
        val packageName = clazz.packageName()
        val superClazz = clazz.superClassType()
        warn("clazz: [pack: $packageName, type: $typeName], super: $superClazz")

        return null
    }


    private fun buildPrimaryKeysString(primaryKeys: List<String>): String {
        val prKeysBuilder = StringBuilder("{ ")
        for (i in primaryKeys.indices) {
            prKeysBuilder.append("\"")
            prKeysBuilder.append(primaryKeys[i])
            prKeysBuilder.append("\"")
            if (i < primaryKeys.size - 1) {
                prKeysBuilder.append(", ")
            }
        }
        prKeysBuilder.append(" }")
        return prKeysBuilder.toString()
    }

}