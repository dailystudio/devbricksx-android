package com.dailystudio.devbricksx.annotations.samples.settings

import com.dailystudio.devbricksx.annotations.data.*

@DataStoreCompanion
open class BasicSettings(
    @IntegerField(10)
    val intType: Int,
    @StringField("abc")
    val stringType: String?,
) {
}

@DataStoreCompanion
class AdvancedSettings(
    intType: Int,
    stringType: String,
    @FloatField(-1f)
    val floatType: Float,
    @BooleanField(true)
    @FieldAlias("it-boolean-or-not")
    val booleanType: Boolean
): BasicSettings(intType, stringType)