package com.dailystudio.devbricksx.annotations.data


@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FIELD)
annotation class IgnoreField

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FIELD)
annotation class FieldAlias(val alias: String)

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FIELD)
annotation class IntegerField(val defaultValue: Int)

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FIELD)
annotation class LongField(val defaultValue: Long)

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FIELD)
annotation class FloatField(val defaultValue: Float)

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FIELD)
annotation class DoubleField(val defaultValue: Double)

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FIELD)
annotation class BooleanField(val defaultValue: Boolean)

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FIELD)
annotation class StringField(val defaultValue: String)
