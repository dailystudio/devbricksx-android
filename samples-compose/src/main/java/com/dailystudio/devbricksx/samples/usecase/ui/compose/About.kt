package com.dailystudio.devbricksx.samples.usecase.ui.compose

import androidx.compose.runtime.Composable
import com.dailystudio.devbricksx.compose.app.AboutDialog
import com.dailystudio.devbricksx.compose.app.AboutInfo
import com.dailystudio.devbricksx.samples.core.R as coreR


@Composable
fun AppAbout(showDialog: Boolean,
             onClose: () -> Unit
) {
    AboutDialog(
        showDialog,
        AboutInfo(
            coreR.string.app_name,
            coreR.string.app_desc,
            coreR.mipmap.ic_launcher,
            coreR.drawable.app_thumb,
        ),
        onClose
    )
}
