package com.dailystudio.devbricksx.samples.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import com.dailystudio.devbricksx.app.activity.DevBricksActivity
import com.dailystudio.devbricksx.samples.usecase.ui.compose.Home

class MainActivity : DevBricksActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SamplesTheme {
                Home()
            }
        }

    }

}

