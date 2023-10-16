package com.dailystudio.devbricksx.notebook.ui.compose

import androidx.compose.runtime.Composable
import com.dailystudio.devbricksx.compose.app.AboutDialog
import com.dailystudio.devbricksx.compose.app.AboutInfo
import com.dailystudio.devbricksx.notebook.core.R


@Composable
fun AppAbout(showDialog: Boolean,
             onClose: () -> Unit
) {
    AboutDialog(
        showDialog,
        AboutInfo(
            R.string.app_name,
            R.string.app_desc,
            R.mipmap.ic_launcher,
            R.drawable.app_thumb,
        ),
        onClose
    )
}
