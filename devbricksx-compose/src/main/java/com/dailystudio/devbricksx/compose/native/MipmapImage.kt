package com.dailystudio.devbricksx.compose.native

import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.dailystudio.devbricksx.utils.ResourcesCompatUtils

@Composable
fun MipmapImage(
    @DrawableRes resId: Int,
    modifier: Modifier = Modifier,
    scaleType: ScaleType = ScaleType.FIT_CENTER

) {
    MipmapImage(
        ResourcesCompatUtils.getDrawable(LocalContext.current, resId),
        modifier,
        scaleType
    )

}
@Composable
fun MipmapImage(
    drawable: Drawable?,
    modifier: Modifier = Modifier,
    scaleType: ScaleType = ScaleType.FIT_CENTER
) {
    AndroidView(
        modifier = modifier,
        factory = { context -> ImageView(context) },
        update = {
            it.scaleType = scaleType
            it.setImageDrawable(drawable)
        }
    )
}