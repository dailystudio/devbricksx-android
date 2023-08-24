package com.dailystudio.devbricksx.samples.usecase

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil.compose.rememberAsyncImagePainter
import com.dailystudio.devbricksx.annotations.compose.ItemContent
import com.dailystudio.devbricksx.annotations.compose.ListScreen
import com.dailystudio.devbricksx.annotations.fragment.DataSource
import com.dailystudio.devbricksx.compose.SingleLineItemContent


@ListScreen(dataSource = DataSource.Flow)
class __UseCase

@ItemContent(UseCase::class)
@Composable
fun UseCaseContent(item: UseCase?, modifier: Modifier = Modifier) {
    SingleLineItemContent(
        item,
        modifier,
        iconOfItem = @Composable { it?.let { rememberAsyncImagePainter(it.icon) } },
        labelOfItem = @Composable { it?.title }
    )
}
