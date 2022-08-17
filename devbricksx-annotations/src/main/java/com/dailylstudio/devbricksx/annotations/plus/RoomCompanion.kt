package com.dailylstudio.devbricksx.annotations.plus

import androidx.room.ForeignKey
import androidx.room.Index
import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class RoomCompanion(
    val primaryKeys: Array<String> = [],
    val autoGenerate: Boolean = false,
    val converters: Array<KClass<*>> = [],
    val foreignKeys: Array<ForeignKey> = [],
    val indices: Array<Index> = [],
    val extension: KClass<*> = Void::class,  // database name
    val database: String = "",  // database version
    val databaseVersion: Int = 1,  // database migrations
    val migrations: Array<KClass<*>> = [],  // dao settings
    val pageSize: Int = 10,  // repository settings
    val repository: Boolean = true
)