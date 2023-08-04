package com.dailystudio.devbricksx.gallery.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.dailystudio.devbricksx.compose.app.AboutDialog
import com.dailystudio.devbricksx.compose.app.AboutInfo
import com.dailystudio.devbricksx.gallery.core.R as coreR


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
