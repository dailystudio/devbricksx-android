package com.dailystudio.devbricksx.ksp.processors.step

import androidx.room.Database
import com.dailylstudio.devbricksx.annotations.plus.RoomCompanion
import com.dailystudio.devbricksx.ksp.GeneratedResult
import com.dailystudio.devbricksx.ksp.GroupedSymbolsProcessStep
import com.dailystudio.devbricksx.ksp.helper.GeneratedNames
import com.dailystudio.devbricksx.ksp.processors.BaseSymbolProcessor
import com.dailystudio.devbricksx.ksp.utils.*
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.TypeSpec

class DatabaseStep(processor: BaseSymbolProcessor)
    : GroupedSymbolsProcessStep(RoomCompanion::class.qualifiedName!!, processor) {

    override fun processSymbolByGroup(
        resolver: Resolver,
        nameOfGroup: String,
        symbols: List<KSClassDeclaration>
    ): GeneratedResult? {
        if (symbols.isEmpty()) {
            return null
        }

        val database = nameOfGroup

        val packageName = symbols.first().packageName()
        val typeNameToGenerate = GeneratedNames.databaseToClassName(database)
        warn("process database [$database]: symbols = $symbols")

        val strOfEntitiesBuilder = StringBuilder("entities = [ ")
        var dbVersion = 1
        for ((i, symbol) in symbols.withIndex()) {
            val companion = symbol.getAnnotation(RoomCompanion::class, resolver)

            companion?.findArgument<Int>("databaseVersion")?.let { dbVerOfSymbol ->
                if (dbVerOfSymbol > dbVersion) {
                    dbVersion = dbVerOfSymbol

                    info("databaseVersion [$dbVersion] of symbol[$symbol] is larger, using it as database version.")
                }
            }

            strOfEntitiesBuilder.append("%N::class")

            if (i < symbols.size - 1) {
                strOfEntitiesBuilder.append(", ")
            }
        }
        strOfEntitiesBuilder.append(" ]")

        val typeNamesOfEntities =
            symbols.map { GeneratedNames.getRoomCompanionName(it.typeName()) }.toTypedArray()

        val databaseAnnotationBuilder: AnnotationSpec.Builder =
            AnnotationSpec.builder(Database::class)
                .addMember(strOfEntitiesBuilder.toString(), *typeNamesOfEntities)
                .addMember("version = %L", dbVersion)

        val classBuilder = TypeSpec.classBuilder(typeNameToGenerate)
//            .superclass(typeOfRoomDatabase())
            .addAnnotation(databaseAnnotationBuilder.build())

        return GeneratedResult(packageName, classBuilder)
    }

    override fun categorizeSymbols(
        resolver: Resolver,
        symbols: Sequence<KSClassDeclaration>
    ): Map<String, List<KSClassDeclaration>> {
        val mapOfDatabase = mutableMapOf<String, MutableList<KSClassDeclaration>>()

        symbols.forEach { symbol ->
            val companion = symbol.getAnnotation(RoomCompanion::class, resolver)
            companion?.let {
                val typeName = symbol.typeName()
                var database = it.findArgument<String?>("database")
                warn("database of symbol [$symbol]: $database")

                if (database.isNullOrEmpty()) {
                    database = GeneratedNames.getRoomCompanionDatabaseName(
                            typeName)
                }

                val symbolsInGroup = if (mapOfDatabase.containsKey(database)) {
                    mapOfDatabase[database]
                } else {
                    mutableListOf<KSClassDeclaration>().also { list ->
                        mapOfDatabase[database] = list
                    }
                }

                symbolsInGroup?.add(symbol)
            }
        }

        return mapOfDatabase
    }

}