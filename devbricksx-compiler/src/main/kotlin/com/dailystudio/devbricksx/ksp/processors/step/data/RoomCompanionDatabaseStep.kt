package com.dailystudio.devbricksx.ksp.processors.step.data

import androidx.room.Database
import androidx.room.TypeConverters
import com.dailystudio.devbricksx.annotations.data.RoomCompanion
import com.dailystudio.devbricksx.ksp.helper.GeneratedNames
import com.dailystudio.devbricksx.ksp.helper.lowerCamelCaseName
import com.dailystudio.devbricksx.ksp.processors.BaseSymbolProcessor
import com.dailystudio.devbricksx.ksp.processors.GeneratedResult
import com.dailystudio.devbricksx.ksp.processors.step.GroupedSymbolsProcessStep
import com.dailystudio.devbricksx.ksp.utils.*
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName

class RoomCompanionDatabaseStep(processor: BaseSymbolProcessor)
    : GroupedSymbolsProcessStep(RoomCompanion::class, processor) {

    companion object {
        private const val INSTANCE_OF_DATABASE = "sInstance"

        private const val METHOD_GET_DATABASE = "getDatabase"
    }

    override fun processSymbolByGroup(
        resolver: Resolver,
        nameOfGroup: String,
        symbols: List<KSClassDeclaration>
    ): List<GeneratedResult> {
        if (symbols.isEmpty()) {
            return emptyResult
        }

        val database = nameOfGroup

        val packageName = symbols.first().packageName()
        val typeNameToGenerate = GeneratedNames.databaseToClassName(database)
        warn("process database [$database]: symbols = $symbols")

        val strOfEntitiesBuilder = StringBuilder("entities = [ ")
        val converters = mutableSetOf<KSType>()
        val migrations = mutableSetOf<KSType>()
        val autoMigrations: MutableList<KSAnnotation> = mutableListOf()

        var dbVersion = 1
        for ((i, symbol) in symbols.withIndex()) {
            val companion = symbol.getAnnotation(RoomCompanion::class) ?: continue
            val companionKS = symbol.getKSAnnotation(RoomCompanion::class, resolver) ?: continue

            if (companion.databaseVersion > dbVersion) {
                dbVersion = companion.databaseVersion
                info("databaseVersion [$dbVersion] of symbol[$symbol] is larger, using it as database version.")
            }

            val convertersInSymbol =
                symbol.collectTypesInAnnotationArguments(
                    RoomCompanion::class, "converters", resolver)
            converters.addAll(convertersInSymbol)

            val migrationsInSymbol =
                symbol.collectTypesInAnnotationArguments(
                    RoomCompanion::class, "migrations", resolver)
            migrations.addAll(migrationsInSymbol)

            val autoMigrationsInSymbol: MutableList<KSAnnotation> =
                companionKS.findArgument("autoMigrations") ?: mutableListOf()
            warn("autoMigrations in symbol [$symbol]: $autoMigrationsInSymbol")
            autoMigrations.addAll(autoMigrationsInSymbol)

            strOfEntitiesBuilder.append("%T::class")
            if (i < symbols.size - 1) {
                strOfEntitiesBuilder.append(", ")
            }
        }
        strOfEntitiesBuilder.append(" ]")

        val typeNamesOfEntities = symbols.map {
            val packageNameOfSymbol = it.packageName()
            val typeNameOfSymbol = it.typeName()

            ClassName(packageNameOfSymbol,
                GeneratedNames.getRoomCompanionName(typeNameOfSymbol))
        }.toTypedArray()

        val databaseAnnotationBuilder: AnnotationSpec.Builder =
            AnnotationSpec.builder(Database::class)
                .addMember(strOfEntitiesBuilder.toString(), *typeNamesOfEntities)
                .addMember("version = %L", dbVersion)

        if (autoMigrations.isNotEmpty()) {
            warn("processing: auto migrations [$autoMigrations]")

            val strOfAutoMigration = autoMigrations.joinToString(separator = ",") {
                val annotationSpec = it.toAnnotationSpec()
                warn("processing: auto migration spec: $annotationSpec")
                annotationSpec.toString()
                    .removePrefix("@")
            }
            warn("processing: new auto migration str = [$strOfAutoMigration]")

            databaseAnnotationBuilder.addMember("autoMigrations = [%L]", strOfAutoMigration)
        }

        val typeOfDatabase = ClassName(packageName, typeNameToGenerate)
        val typeOfContext = TypeNameUtils.typeOfContext()
        val typeOfMigration = TypeNameUtils.typeOfMigration()
        val typeOfArrayOfMigrations = ARRAY.parameterizedBy(typeOfMigration)

        val classBuilder = TypeSpec.classBuilder(typeNameToGenerate)
            .superclass(TypeNameUtils.typeOfRoomDatabase())
            .addAnnotation(databaseAnnotationBuilder.build())
            .addModifiers(KModifier.ABSTRACT)

        val classCompanionBuilder = TypeSpec.companionObjectBuilder()

        if (converters.isNotEmpty()) {
            val strOfConvertersBuilder = converters.joinToString(separator = ",") {
                "%T::class"
            }

            val typesOfConverters = converters.map { it.toTypeName() }.toTypedArray()
            warn("adding type converters: $typesOfConverters, str = $strOfConvertersBuilder")
            val typeConvertersAnnotationBuilder =
                AnnotationSpec.builder(TypeConverters::class)
                    .addMember(strOfConvertersBuilder,
                        *typesOfConverters)

            classBuilder.addAnnotation(typeConvertersAnnotationBuilder.build())
        }

        val instancePropBuilder = PropertySpec.builder(INSTANCE_OF_DATABASE, typeOfDatabase.copy(nullable = true))
            .addModifiers(KModifier.PRIVATE)
            .mutable(true)
            .initializer("null")

        classCompanionBuilder.addProperty(instancePropBuilder.build())

        val methodGetDatabaseWithMigrationsBuilder = FunSpec.builder(METHOD_GET_DATABASE)
            .addParameter("context", typeOfContext)
            .addParameter("migrations", typeOfArrayOfMigrations.copy(nullable = true))
            .addAnnotation(AnnotationSpec.builder(Synchronized::class).build())
            .returns(typeOfDatabase)
            .addStatement("var database = %N", INSTANCE_OF_DATABASE)
            .beginControlFlow("if (database == null)")
            .addStatement("""
                val builder = %T.databaseBuilder(
                    context.getApplicationContext(), 
                    %T::class.java, 
                    "%L")
                """.trimIndent(),
                TypeNameUtils.typeOfRoom(),
                typeOfDatabase,
                database)
            .beginControlFlow("if (migrations != null)")
            .addStatement("builder.addMigrations(*migrations)")
            .endControlFlow()

        if (dbVersion > 1) {
            val typeOfDummyMigration =
                TypeNameUtils.typeOfDummyMigration()

            methodGetDatabaseWithMigrationsBuilder.beginControlFlow("else")

            methodGetDatabaseWithMigrationsBuilder.addStatement(
                "val customizedMigrations = mutableListOf<%T>()",
                typeOfMigration)
            if (migrations.isEmpty()) {
                methodGetDatabaseWithMigrationsBuilder.addStatement(
                    "customizedMigrations.add(%T(1, %L))",
                    typeOfDummyMigration, dbVersion)
            } else {
                for (migration in migrations) {
                    methodGetDatabaseWithMigrationsBuilder.addStatement(
                        "customizedMigrations.add(%T())",
                        migration.toTypeName()
                    )
                }
            }

            methodGetDatabaseWithMigrationsBuilder
                .addStatement("builder.addMigrations(*customizedMigrations.toTypedArray())")
                .endControlFlow()
        }

        methodGetDatabaseWithMigrationsBuilder
            .addStatement("database = builder.build()")
            .addStatement("%N = database", INSTANCE_OF_DATABASE)
            .endControlFlow()
            .addStatement("return database")

        classCompanionBuilder.addFunction(methodGetDatabaseWithMigrationsBuilder.build())

        val methodGetDatabaseDefaultBuilder = FunSpec.builder(METHOD_GET_DATABASE)
            .addParameter("context", typeOfContext)
            .addStatement("return %N(context, null)", METHOD_GET_DATABASE)
            .returns(typeOfDatabase)
        classCompanionBuilder.addFunction(methodGetDatabaseDefaultBuilder.build())

        classBuilder.addType(classCompanionBuilder.build())

        for (symbol in symbols) {
            val packageNameOfSymbol = symbol.packageName()
            val typeNameOfSymbol = symbol.typeName()

            val nameOfDao = GeneratedNames.getRoomCompanionDaoName(typeNameOfSymbol)
            val typeOfDao = ClassName(packageNameOfSymbol, nameOfDao)

            val methodDaoBuilder = FunSpec.builder(nameOfDao.lowerCamelCaseName())
                .addModifiers(KModifier.ABSTRACT, KModifier.PUBLIC)
                .returns(typeOfDao)

            classBuilder.addFunction(methodDaoBuilder.build())
        }

        return singleClassResult(symbols, packageName, classBuilder)
    }

    override fun categorizeSymbols(
        resolver: Resolver,
        symbols: Sequence<KSClassDeclaration>
    ): Map<String, List<KSClassDeclaration>> {
        val mapOfDatabase = mutableMapOf<String, MutableList<KSClassDeclaration>>()

        symbols.forEach { symbol ->
            val companion = symbol.getAnnotation(RoomCompanion::class) ?: return@forEach
            val typeName = symbol.typeName()
            var database = companion.database
            warn("database of symbol [$symbol]: $database")

            if (database.isEmpty()) {
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

        return mapOfDatabase
    }

}
