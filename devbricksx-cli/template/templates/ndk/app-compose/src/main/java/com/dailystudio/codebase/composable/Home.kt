package com.dailystudio.codebase.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.dailystudio.codebase.core.R
import com.dailystudio.codebase.compose.codebaseTopAppBarColors
import com.dailystudio.codebase.core.nativelib.NativeLib

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home() {
    val nativeLib = NativeLib()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                colors = codebaseTopAppBarColors(),
            )
        },
        content = { padding ->
            Box (
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = nativeLib.getString(LocalContext.current, R.string.hello),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        },
    )

}