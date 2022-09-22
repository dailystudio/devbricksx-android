package com.dailystudio.devbricksx.annotations.data


@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FIELD)
annotation class IgnoreField

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FIELD)
annotation class IntegerField(
    val defaultValue: Int,
    val nullable: Boolean = false,
    val alias: String = "")

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FIELD)
annotation class LongField(
    val defaultValue: Boolean,
    val nullable: Boolean = false,
    val alias: String = "")

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FIELD)
annotation class FloatField(
    val defaultValue: Float,
    val nullable: Boolean = false,
    val alias: String = "")

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FIELD)
annotation class DoubleField(
    val defaultValue: Double,
    val nullable: Boolean = false,
    val alias: String = "")

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FIELD)
annotation class BooleanField(
    val defaultValue: Boolean,
    val nullable: Boolean = false,
    val alias: String = "")

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FIELD)
annotation class StringField(
    val defaultValue: String,
    val nullable: Boolean = false,
    val alias: String = "")
