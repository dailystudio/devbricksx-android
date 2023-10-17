package com.dailystudio.devbricksx.notebook.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import com.dailystudio.devbricksx.compose.app.OptionMenuItem
import com.dailystudio.devbricksx.compose.app.OptionMenus
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.notebook.core.R
import com.dailystudio.devbricksx.notebook.db.Notebook
import com.dailystudio.devbricksx.notebook.theme.notebookTopAppBarColors

const val MENU_ITEM_ID_ABOUT = 0x1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotebooksPage(
    onItemClick: (item: Notebook) -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                colors = notebookTopAppBarColors(),
                actions = {
                    IconButton(onClick = {
                        showMenu = true
                    }) {
                        Icon(Icons.Default.MoreVert, "More actions")
                    }

                    NotebooksMenus(
                        showMenu = showMenu,
                        onMenuDismissed = { showMenu = false }) {
                        when(it) {
                            MENU_ITEM_ID_ABOUT -> showAboutDialog = true
                        }
                    }

                }
            )


        },
        content = { padding ->
            Logger.debug("padding: $padding")
            Column (
                modifier = Modifier.padding(padding)
            ) {
                NotebooksScreen(selectKey = {
                    it?.id ?: -1
                })
                AppAbout(showDialog = showAboutDialog) {
                    showAboutDialog = false
                }
            }
        }
    )
}


@Composable
fun NotebooksMenus(
    showMenu: Boolean,
    modifier: Modifier = Modifier,
    menuOffset: DpOffset = DpOffset.Zero,
    onMenuDismissed: () -> Unit,
    onMenuItemClick: (Int) -> Unit
) {
    val items = setOf(
        OptionMenuItem(
            MENU_ITEM_ID_ABOUT,
            stringResource(R.string.menu_about),
            rememberVectorPainter(Icons.Filled.Info)
        )
    )

    OptionMenus(
        showMenu, items,
        modifier, menuOffset,
        onMenuDismissed  = onMenuDismissed,
        onMenuItemClick = onMenuItemClick
    )
}
