package com.dailystudio.devbricksx.samples.usecase.ui.compose

import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dailystudio.devbricksx.compose.app.OptionMenuItem
import com.dailystudio.devbricksx.compose.app.OptionMenus
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.samples.compose.samplesTopAppBarColors
import com.dailystudio.devbricksx.samples.core.R
import com.dailystudio.devbricksx.samples.usecase.model.UseCaseViewModelExt

const val MENU_ITEM_ID_ABOUT = 0x1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UseCasesScreenExt() {
    var showMenu by remember { mutableStateOf(false) }

    var showAboutDialog by remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                colors = samplesTopAppBarColors(),
                actions = {
                    Box {
                        IconButton(onClick = {
                            showMenu = true
                        }) {
                            Icon(Icons.Default.MoreVert, "More actions")
                        }

                        MainMenus(showMenu = showMenu,
                            onMenuDismissed = { showMenu = false }) {
                            when(it) {
                                MENU_ITEM_ID_ABOUT -> showAboutDialog = true
                            }
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
                UseCasesScreen(
                    modifier = Modifier.padding(8.dp),
                    dataSource = @Composable {
                        val viewModel = viewModel<UseCaseViewModelExt>()
                        val data by viewModel.allUseCasesFlow.collectAsState(emptyList())
                        data
                    },
                    onItemClicked = {
                        Logger.debug("click on case: $it")
                    },
                    onItemLongClicked = {
                        Logger.debug("long click on case: $it")
                    }
                )

                AppAbout(showDialog = showAboutDialog) {
                    showAboutDialog = false
                }
            }
        },
    )

}


@Composable
fun MainMenus(
    showMenu: Boolean,
    modifier: Modifier = Modifier,
    menuOffset: DpOffset = DpOffset.Zero,
    onMenuDismissed: () -> Unit,
    onMenuItemClick: (Int) -> Unit
) {
    val items = setOf(
        OptionMenuItem(
            MENU_ITEM_ID_ABOUT,
            stringResource(R.string.action_about),
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
