package com.dailystudio.devbricksx.notebook.ui.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dailystudio.devbricksx.compose.app.BackPressHandler
import com.dailystudio.devbricksx.development.Logger
import kotlinx.coroutines.delay
import kotlin.math.roundToInt


@Composable
fun <T, I> BaseNoteItemsPage(
    itemId: (item: T) -> I,
    actionBar: @Composable () -> Unit = {},
    fabContent: @Composable () -> Unit = {},
    onFabClicked: () -> Unit = {},
    onItemsAction: (items: Set<I>, action: String) -> Unit,
    itemsContent: @Composable (
        modifier: Modifier,
        selectable: Boolean,
        onSelectionStarted: (item: T) -> Unit,
        onSelectionEnded: (item: T) -> Unit,
        onSelectItem: (item: T) -> Unit,
    ) -> Unit,
) {
    var showDeletionConfirmDialog by remember { mutableStateOf(false) }
    var inSelectionMode by remember { mutableStateOf(false) }
    var fabVisible by remember { mutableStateOf(false) }

    val selectedIds = remember { mutableStateMapOf<I, Boolean>() }

    val beginSelection = {
        inSelectionMode = true
        fabVisible = false
    }

    val endSelection = {
        inSelectionMode = false
        fabVisible = true

        selectedIds.clear()
    }

    LaunchedEffect(key1 = true) {
        delay(400)
        fabVisible = true
    }

    Scaffold(
        topBar = {
            if (inSelectionMode) {
                ItemSelectionTopBar(
                    selection = selectedIds.keys.toSet(),
                    onSelectionCancelled = { endSelection() },
                    onSelectionActionPerformed = { _, action ->
                        when (action) {
                            ACTION_DELETE ->  showDeletionConfirmDialog = true
                        }
                    }
                )
            } else {
                actionBar()
            }
        },
        floatingActionButton = {
            AnimatedVisibility(fabVisible,
                enter = slideInHorizontally(initialOffsetX = {it}),
                exit = slideOutHorizontally(targetOffsetX = { (it * 1.2).roundToInt()})
            ) {
                FloatingActionButton(
                    modifier = Modifier.padding(8.dp),
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary,
                    onClick = onFabClicked
                ) {
                    fabContent()
                }
            }
        },
        content = { padding ->
            Logger.debug("padding: $padding")
            BackPressHandler(inSelectionMode) {
                endSelection()
            }

            Column (
                modifier = Modifier.padding(padding)
            ) {
                itemsContent(
                    Modifier,
                    inSelectionMode,
                    {
                        beginSelection()
                        selectedIds[itemId(it)] = true
                    },
                    {
                        endSelection()
                    },
                    {
                        val id = itemId(it)
                        Logger.debug("on selected: $it")
                        val selected =
                            selectedIds.containsKey(id)
                        if (selected) {
                            selectedIds.remove(id)
                        } else {
                            selectedIds[id] = true
                        }

                        Logger.debug("after selected: $selectedIds")
                    },
                )

                DeletionConfirmDialog(
                    showDialog = showDeletionConfirmDialog,
                    onCancel = { showDeletionConfirmDialog = false }) {
                    showDeletionConfirmDialog = false

                    onItemsAction(selectedIds.keys.toSet(), ACTION_DELETE)

                    endSelection()
                }
            }
        }
    )
}