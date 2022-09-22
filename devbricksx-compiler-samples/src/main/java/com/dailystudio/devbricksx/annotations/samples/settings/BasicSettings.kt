package com.dailystudio.devbricksx.annotations.samples.settings

import com.dailystudio.devbricksx.annotations.data.DataStoreCompanion

@DataStoreCompanion
open class BasicSettings(
    val intType: Int,
    val stringType: String,
) {
}

@DataStoreCompanion
class AdvancedSettings(
    intType: Int,
    stringType: String,
    val floatType: Float,
    val booleanType: Boolean
): BasicSettings(intType, stringType)