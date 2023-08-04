package com.dailystudio.devbricksx.compose.native

import android.text.util.Linkify
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun StyledText(
    text: CharSequence,
    modifier: Modifier = Modifier,
    autoLink: Int = -1) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextView(context).apply {
                if (autoLink > 0) {
                    autoLinkMask = autoLink
                }
            }
        },
        update = {
            it.text = text
        }
    )
}