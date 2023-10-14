package com.dailystudio.devbricksx.notebook.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import com.dailystudio.devbricks.notebook.composable.Home
import com.dailystudio.devbricksx.notebook.theme.NotebookTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NotebookTheme {
                Home()
            }
        }
    }
}
