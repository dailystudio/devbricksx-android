package com.dailystudio.devbricksx.gallery.composable

import android.view.LayoutInflater
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.dailystudio.devbricksx.compose.R as composeR
import com.dailystudio.devbricksx.gallery.core.R as coreR


@Composable
fun AboutDialog(showDialog: Boolean,
                onClose: () -> Unit
) {
    if (showDialog) {
        Dialog(
            onDismissRequest = {
            },
        ) {
            Column(
                Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Image(
                    painter = painterResource(id = coreR.drawable.app_thumb),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.height(200.dp).fillMaxWidth()
                )
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AndroidView(
                        factory = {
                            val view = LayoutInflater.from(it).inflate(
                                composeR.layout.layout_launcher_icon, null, false)
                            view
                        },
                        modifier = Modifier
                            .size(72.dp)
                    )
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = stringResource(id = coreR.string.app_name),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(text = "1.0.0",
                            style = MaterialTheme.typography.bodyMedium
                        )

                    }
                }
                AndroidView(
                    factory = {
                        val view = LayoutInflater.from(it).inflate(
                            composeR.layout.layout_description_text, null, false)
                        view
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    TextButton(onClick = {
                        onClose()
                    }) {
                        Text(stringResource(id = android.R.string.ok))
                    }
                }
            }
        }
    }
}
