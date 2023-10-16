package com.dailystudio.devbricksx.gallery.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dailystudio.devbricksx.development.Logger

@Preview
@Composable
fun AClip() {
    Chip("A", null){}
}

@Composable
fun Chip(label: String,
         icon: Painter? = null,
         onChipClick: () -> Unit
) {
 /*   Surface(
        modifier = Modifier.wrapContentHeight()
            .width(100.dp),
        color = Color.Blue,
        shape = RoundedCornerShape(16.dp),

    ) {
        Text(
            label,
            color = Color.White,
            modifier = Modifier,
            textAlign = TextAlign.Center
        )
    }*/
    AssistChip(
        onClick = {
            Logger.debug("clicked")
            onChipClick()
        },
        label = {
            Text(
                label,
                modifier = Modifier
                    .absolutePadding(left = 4.dp),
                style = MaterialTheme.typography.labelMedium
                    .copy(color = Color.DarkGray),
                textAlign = TextAlign.Center
            )
        },
        trailingIcon = {
            Icon(icon!!,
            modifier = Modifier
                .padding(horizontal = 6.dp),
            contentDescription = null)
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = Color.White,
        ),
        border = null,
        shape = MaterialTheme.shapes.large
    )
}