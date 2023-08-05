package com.dailystudio.devbricksx.gallery.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dailystudio.devbricksx.compose.app.OptionMenuItem
import com.dailystudio.devbricksx.compose.app.OptionMenus
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.gallery.Constants
import com.dailystudio.devbricksx.gallery.model.PhotoItemViewModelExt
import com.dailystudio.devbricksx.gallery.core.R as coreR

const val MENU_ITEM_ID_ABOUT = 0x1

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Home() {
    val viewModel = viewModel<PhotoItemViewModelExt>()

    val queryOfPhotos by viewModel.photoQuery.observeAsState()
    Logger.debug("home recompose: $queryOfPhotos")
    var showMenu by remember { mutableStateOf(false) }

    var showAboutDialog by remember {
        mutableStateOf(false)
    }

    var searchActivated by remember {
        mutableStateOf(false)
    }

    Scaffold(topBar = {
        val focusRequester = remember { FocusRequester() }
        val keyboardController = LocalSoftwareKeyboardController.current

        val queryValue = queryOfPhotos ?: Constants.QUERY_ALL
        val querySelectionIndex = queryValue.length
        Logger.debug("queryValue recompose: $queryValue")

        val queryInputState = remember {
            mutableStateOf(
                TextFieldValue(
                    text = if (queryValue == Constants.QUERY_ALL) {
                        ""
                    } else {
                        queryValue
                    },
                    selection = TextRange(querySelectionIndex)
                )
            )
        }


        if (searchActivated) {
            TopAppBar(
                title = {
//                    Text(text = stringResource(id = R.string.app_name))
                },
                navigationIcon = {
                    if (searchActivated) {
                        IconButton(onClick = {
                            searchActivated = false
                        }) {
                            Icon(Icons.Default.ArrowBack, "Close Search")
                        }
                    }
                },
                actions = {
                    var xOffsetOfSearchInPx = 0f
                    TextField(
                        value = queryInputState.value,
                        onValueChange = {
                            queryInputState.value = it
                        },
                        placeholder = {
                            Text(
                                text = stringResource(id = coreR.string.hint_title),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.White.copy(
                                        alpha = LocalContentColor.current.alpha
                                    ),
                                ),
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            cursorColor = MaterialTheme.colorScheme.secondaryContainer,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                        ),
                        textStyle = MaterialTheme.typography.headlineMedium,
                        keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                var newQuery = queryInputState.value.text
                                if (newQuery.isBlank()) {
                                    newQuery = Constants.QUERY_ALL
                                }
                                viewModel.searchPhotos(newQuery)
                                keyboardController?.hide()
                                searchActivated = false
                            }
                        ),
                        modifier = Modifier.focusRequester(focusRequester)
                            .fillMaxWidth(.8f)
                            .onGloballyPositioned {
                                xOffsetOfSearchInPx = it.positionInRoot().x
                            }
                    )

                    DisposableEffect(Unit) {
                        focusRequester.requestFocus()
                        onDispose { }
                    }

                    IconButton(onClick = {
                        showMenu = true
                    }) {
                        Icon(Icons.Default.MoreVert, "More actions")
                    }

                    val density = LocalDensity.current.density
                    var marginToEndOfScreenInPx = with(LocalDensity.current) {
                        (LocalConfiguration.current.screenWidthDp.dp.roundToPx() - xOffsetOfSearchInPx)
                    }

                    MainMenus(modifier = Modifier.onGloballyPositioned {
                            marginToEndOfScreenInPx -= it.size.width
                        },
                        menuOffset = DpOffset(
                            (marginToEndOfScreenInPx / density).dp,
                            0.dp
                        ),
                        showMenu = showMenu,
                        onMenuDismissed = { showMenu = false }) {
                        when(it) {
                            MENU_ITEM_ID_ABOUT -> showAboutDialog = true
                        }
                    }

                }
            )
        } else {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = coreR.string.app_name))
                },
                actions = {
                    var xOffsetOfSearchInPx = 0f
                    IconButton(
                        onClick = {
                            searchActivated = true
                        },
                        modifier = Modifier.onGloballyPositioned {
                            xOffsetOfSearchInPx = it.positionInRoot().x
                        }
                    ) {
                        Icon(Icons.Default.Search, "Search")
                    }

                    if (queryOfPhotos != Constants.QUERY_ALL) {
                        Chip(
                            label = queryInputState.value.text,
                            icon = painterResource(id = coreR.drawable.ic_action_search_clear)
                        ) {
                            Logger.debug("clear search")
                            viewModel.searchPhotos(Constants.QUERY_ALL)
                        }
                    }

                    IconButton(onClick = {
                        showMenu = true
                    }) {
                        Icon(Icons.Default.MoreVert, "More actions")
                    }

                    val density = LocalDensity.current.density
                    var marginToEndOfScreenInPx = with(LocalDensity.current) {
                        (LocalConfiguration.current.screenWidthDp.dp.roundToPx() - xOffsetOfSearchInPx)
                    }

                    MainMenus(modifier = Modifier.onGloballyPositioned {
                        marginToEndOfScreenInPx -= it.size.width
                    },
                        menuOffset = DpOffset(
                            (marginToEndOfScreenInPx / density).dp,
                            0.dp
                        ),
                        showMenu = showMenu,
                        onMenuDismissed = { showMenu = false }) {
                        when(it) {
                            MENU_ITEM_ID_ABOUT -> showAboutDialog = true
                        }
                    }
                }
            )
        }

    }, content = { padding ->
        Column (
            modifier = Modifier.padding(padding)
        ) {
            PhotoItemsScreenExt()

            AppAbout(showDialog = showAboutDialog) {
                showAboutDialog = false
            }
        }
    })
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
            stringResource(coreR.string.menu_about),
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
