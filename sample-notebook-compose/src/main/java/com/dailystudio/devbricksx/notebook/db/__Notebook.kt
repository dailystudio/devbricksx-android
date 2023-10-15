package com.dailystudio.devbricksx.notebook.db

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dailystudio.devbricksx.annotations.compose.ItemContent
import com.dailystudio.devbricksx.annotations.compose.ListScreen
import com.dailystudio.devbricksx.annotations.fragment.DataSource
import com.dailystudio.devbricksx.development.Logger


@ItemContent(Notebook::class)
@Composable
fun NotebookContent(item: Notebook?, modifier: Modifier) {
    Logger.debug("item: $item")
    Card(modifier = Modifier
        .height(250.dp)
        .padding(8.dp),
//                elevation = 3.dp,
        shape = MaterialTheme.shapes.medium.copy(
            CornerSize(5.dp)
        )
    ) {
        Text(
           text = item?.name ?: "Empty"
        )
    }
}

@ListScreen(paged = true, dataSource = DataSource.Flow, gridLayout = true, columns = 2)
class __Notebook

@ListScreen(paged = true, dataSource = DataSource.Flow, gridLayout = true, columns = 2)
class __Note

