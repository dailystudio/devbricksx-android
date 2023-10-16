package com.dailystudio.devbricksx.gallery.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.dailystudio.devbricksx.compose.app.OptionMenuItem
import com.dailystudio.devbricksx.compose.app.OptionMenus
import com.dailystudio.devbricksx.compose.utils.activityViewModel
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.gallery.Constants
import com.dailystudio.devbricksx.gallery.theme.galleryTopAppBarColors
import com.dailystudio.devbricksx.gallery.core.R
import com.dailystudio.devbricksx.gallery.db.PhotoItem
import com.dailystudio.devbricksx.gallery.model.PhotoItemViewModelExt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun PhotosScreen(
    onItemClick: (item: PhotoItem) -> Unit
) {
    val viewModel = activityViewModel<PhotoItemViewModelExt>()

    val queryOfPhotos by viewModel.photoQuery.observeAsState()
    Logger.debug("home recompose: $queryOfPhotos")
    var showMenu by remember { mutableStateOf(false) }

    var showAboutDialog by remember {
        mutableStateOf(false)
    }

    var searchActivated by remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
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

            val doSearch = {
                var newQuery = queryInputState.value.text
                if (newQuery.isBlank()) {
                    newQuery = Constants.QUERY_ALL
                }

                Logger.debug("do searching for: $newQuery")
                viewModel.searchPhotos(newQuery)
            }

            val clearSearch = {
                queryInputState.value = TextFieldValue("")
                doSearch()
            }

            val keyboardController = LocalSoftwareKeyboardController.current

            TopAppBar(
                title = {
                    if(!searchActivated) {
                        Text(text = stringResource(id = R.string.app_name))
                    }
                },
                colors = galleryTopAppBarColors(),
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

                    if (searchActivated) {
                        SearchBar(
                            queryInputState.value,
                            onInputChange = {
                                queryInputState.value = it
                            },
                            onInputClear = {
                                queryInputState.value = TextFieldValue("")
                            },
                            onSearch = {
                                doSearch()
                                keyboardController?.hide()
                                searchActivated = false
                            }
                        )
                    } else {
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
                                icon = painterResource(id = R.drawable.ic_action_search_clear)
                            ) {
                                Logger.debug("clear search")
                                clearSearch()
                            }
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

                    MainMenus(/*modifier = Modifier.onGloballyPositioned {
                        marginToEndOfScreenInPx -= it.size.width
                    },
                        menuOffset = DpOffset(
                            (marginToEndOfScreenInPx / density).dp,
                            0.dp
                        ),*/
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
                PhotoItemsScreenExt(onItemClick = onItemClick)

                AppAbout(showDialog = showAboutDialog) {
                    showAboutDialog = false
                }
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    )
}


@Composable
fun SearchBar(
    queryInput: TextFieldValue,
    onInputChange: (TextFieldValue) -> Unit,
    onInputClear: () -> Unit,
    onSearch: (TextFieldValue) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    TextField(
        value = queryInput,
        onValueChange = {
            onInputChange(it)
        },
        placeholder = {
            Icon(Icons.Default.Search, "Search",
                Modifier.fillMaxHeight()
            )
        },
        colors = TextFieldDefaults.colors(
            focusedPlaceholderColor = MaterialTheme.colorScheme.onPrimary,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onPrimary,
            focusedTextColor = MaterialTheme.colorScheme.onPrimary,
            cursorColor = MaterialTheme.colorScheme.onPrimary,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
        ),
        textStyle = MaterialTheme.typography.headlineSmall,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch(queryInput)
            }
        ),
        modifier = Modifier.focusRequester(focusRequester)
            .fillMaxWidth(.65f)
    )

    DisposableEffect(Unit) {
        focusRequester.requestFocus()
        onDispose { }
    }

    IconButton(onClick = {
        onInputClear()
    }) {
        Icon(Icons.Default.Clear, "clear")
    }
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
