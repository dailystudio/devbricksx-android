package com.dailystudio.devbricksx.notebook.db

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.dailystudio.devbricksx.annotations.compose.ItemContent
import com.dailystudio.devbricksx.annotations.compose.ListScreen
import com.dailystudio.devbricksx.annotations.fragment.DataSource
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.notebook.core.R

@ItemContent(Notebook::class)
@Composable
fun NotebookContent(
    notebook: Notebook?,
    modifier: Modifier = Modifier,
    selectable: Boolean,
    selected: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        ConstraintLayout(
            modifier = modifier
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxWidth()
                .height(64.dp)
        ) {
            val (icon, name, count, indicator) = createRefs()

            Icon(
                tint = MaterialTheme.colorScheme.primary,
                painter = painterResource(id = R.drawable.ic_notebook),
                contentDescription = null,
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 10.dp)
                    .constrainAs(icon) {
                        start.linkTo(parent.start)
                        centerVerticallyTo(parent)
                    }
                    .size(44.dp)
            )

            val counts = (notebook as? NotebookInfo)?.notesCount ?: 0
            if (counts > 0) {
                Text(
                    text = counts.toString(),
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier
                        .constrainAs(count) {
                            end.linkTo(parent.end, margin = 10.dp)
                            width = Dimension.fillToConstraints
                            centerVerticallyTo(parent)
                        }
                )
            }

            Text(
                text = notebook?.name ?: "",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium.copy(
                    textAlign = TextAlign.Left
                ),
                modifier = Modifier
                    .constrainAs(name) {
                        start.linkTo(icon.end)
                        end.linkTo(count.start)
                        width = Dimension.fillToConstraints
                        centerVerticallyTo(parent)
                    }
            )


            if (selected) {
                val indicatorColor = MaterialTheme.colorScheme.primary
                Canvas(
                    modifier = Modifier
                        .constrainAs(indicator) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)

                            height = Dimension.fillToConstraints
                        }
                        .width(8.dp)

                ) {
                    drawRect(indicatorColor, size = size)
                }
            }
        }

    }

}

@ListScreen(paged = true,
    dataSource = DataSource.Flow,
    gridLayout = true,
    columns = 2,
    selectable = true
)
class __Notebook


@ItemContent(Note::class)
@Composable
fun NoteContent(
    note: Note?,
    modifier: Modifier = Modifier,
    selectable: Boolean = false,
    selected: Boolean,
) {
    Card(
        modifier = modifier
            .padding(all = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        ConstraintLayout(modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Top)
            .heightIn(min = 190.dp, max = 250.dp)
        ) {
            val (content, indicator) = createRefs()

            Column(
                modifier = Modifier
                    .constrainAs(content) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)

                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
                    .padding(16.dp)
            ) {
                Text(
                    text = note?.title ?: "",
                    maxLines = 2,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 18.sp,
                        textAlign = TextAlign.Left,
                        fontWeight = FontWeight.Bold
                    ),
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = note?.desc ?: "Empty",
                    overflow = TextOverflow.Clip,
                    maxLines = 6,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,

                        textAlign = TextAlign.Left
                    ),
                )
            }


            if (selected) {
                val indicatorColor = MaterialTheme.colorScheme.primary
                Canvas(
                    modifier = Modifier
                        .constrainAs(indicator) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)

                            height = Dimension.fillToConstraints
                        }
                        .width(8.dp)

                ) {
                    drawRect(indicatorColor, size = size)
                }
            }
        }

    }

}


@ListScreen(
    paged = true,
    dataSource = DataSource.Flow,
    gridLayout = true,
    columns = 2,
    selectable = true
)
class __Note

