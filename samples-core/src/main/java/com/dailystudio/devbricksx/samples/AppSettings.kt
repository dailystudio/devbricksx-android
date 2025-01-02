package com.dailystudio.devbricksx.samples

import com.dailystudio.devbricksx.annotations.data.BooleanField
import com.dailystudio.devbricksx.annotations.data.DataStoreCompanion

@DataStoreCompanion
class AppSettings(
    @BooleanField(false)
    val useAnimation: Boolean = false,
) {
    companion object {

    }
}
