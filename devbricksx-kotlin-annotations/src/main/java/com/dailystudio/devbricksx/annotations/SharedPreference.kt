package com.dailystudio.devbricksx.annotations

@Deprecated("SharedPreference is deprecated by DataStoreCompanion in package com.dailystudio.devbricksx.annotations.data")
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class SharedPreference()

@Deprecated("PreferenceValue is deprecated by IntegerField/LongField/FloatField/BooleanField/StringField in package com.dailystudio.devbricksx.annotations.fragment")
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FIELD)
annotation class PreferenceValue(val prefName: String = "",
                                 val defaultValueStr: String = "")