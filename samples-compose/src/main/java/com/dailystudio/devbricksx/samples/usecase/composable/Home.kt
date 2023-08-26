package com.dailystudio.devbricksx.samples.usecase.composable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dailystudio.devbricksx.compose.app.OptionMenuItem
import com.dailystudio.devbricksx.compose.app.OptionMenus
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.samples.core.R
import com.dailystudio.devbricksx.samples.usecase.compose.UseCasesScreen
import com.dailystudio.devbricksx.samples.usecase.model.UseCaseViewModelExt

const val MENU_ITEM_ID_ABOUT = 0x1

@Composable
fun Home() {
    UseCasesScreen(
        dataSource = @Composable {
            val viewModel = viewModel<UseCaseViewModelExt>()
            val data by viewModel.allUseCasesFlow.collectAsState(emptyList())
            data
        },
        onItemClick = {
            Logger.debug("click on case: $it")
        }
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
