package com.dailystudio.devbricksx.compose.app

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

data class OptionMenuItem(
    val id: Int,
    val label: String,
    val icon: ImageVector,
)

@Composable
fun OptionMenus(
    showMenu: Boolean,
    menuItems: Collection<OptionMenuItem>,
    modifier: Modifier = Modifier,
    menuOffset: DpOffset = DpOffset.Zero,
    onMenuDismissed: () -> Unit,
    onMenuItemClick: (Int) -> Unit
) {
    DropdownMenu(
        modifier = modifier,
        offset = menuOffset,
        expanded = showMenu,
        onDismissRequest = onMenuDismissed,
    ) {
        menuItems.forEach { item ->
            DropdownMenuItem(onClick = {
                onMenuItemClick(item.id)
                onMenuDismissed()
            }, text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.widthIn(min = 100.dp)
                ) {
                    Icon(
                        item.icon,
                        contentDescription = null,
                        modifier = Modifier.padding(8.dp)
                    )
                    Text(item.label)
                }
            })
        }

    }
}