package com.dailystudio.devbricksx.compose.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
typealias PermissionsScreenContent =
        @Composable (allGranted: Boolean, state: MultiplePermissionsState) -> Unit

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsScreen(
    permissions: List<String>,
    content: PermissionsScreenContent
) {
    val multiplePermissionsState =
        rememberMultiplePermissionsState(permissions)

    content(
        multiplePermissionsState.allPermissionsGranted,
        multiplePermissionsState
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsScreen(
    permission: String,
    content: PermissionsScreenContent
) = PermissionsScreen(listOf(permission), content)
