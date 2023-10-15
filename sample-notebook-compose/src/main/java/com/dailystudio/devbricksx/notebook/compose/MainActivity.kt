package com.dailystudio.devbricksx.notebook.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.dailystudio.devbricksx.notebook.DummyDataUtils
import com.dailystudio.devbricksx.notebook.composable.Home
import com.dailystudio.devbricksx.notebook.theme.NotebookTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DummyDataUtils.createSampleNotes(this, this.lifecycleScope)
        setContent {
            NotebookTheme {
                Home()
            }
        }
    }
}
