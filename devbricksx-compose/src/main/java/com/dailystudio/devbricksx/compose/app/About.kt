package com.dailystudio.devbricksx.compose.app

import android.text.util.Linkify
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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
import androidx.compose.ui.window.Dialog
import com.dailystudio.devbricksx.compose.native.MipmapImage
import com.dailystudio.devbricksx.compose.native.StyledText

data class AboutInfo(
    @StringRes val appName: Int,
    @StringRes val appDesc: Int,
    @DrawableRes val appIcon: Int,
    @DrawableRes val appThumb: Int,
)

@Composable
fun AboutDialog(showDialog: Boolean,
                info: AboutInfo,
                onClose: () -> Unit
) {
    if (showDialog) {
        Dialog(
            onDismissRequest = {
            },
        ) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                About(info)

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


@Composable
fun About(
    info: AboutInfo,
) {
    Column {
        Image(
            painter = painterResource(id = info.appThumb),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
        )
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MipmapImage(
                info.appIcon,
                modifier = Modifier.size(72.dp)
            )
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = stringResource(id = info.appName),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(text = "1.0.0",
                    style = MaterialTheme.typography.bodyMedium
                )

            }
        }
        StyledText(
            stringResource(id = info.appDesc),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            autoLink = Linkify.WEB_URLS or Linkify.EMAIL_ADDRESSES
        )

    }
}
