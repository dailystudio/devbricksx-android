package com.dailystudio.devbricksx.notebook.ui.compose

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.dailystudio.devbricksx.notebook.core.R

@Composable
fun NewNotebookDialog(showDialog: Boolean,
                      onCancel: () -> Unit,
                      onNewNotebook: (String) -> Unit
) {
    var notebookName by remember {
        mutableStateOf("")
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
            },

            title = {
                Text(
                    text = stringResource(id = R.string.dialog_title_new_notebook),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                )
            },

            text = {
                TextField(
                    value = notebookName,
                    onValueChange = {
                        notebookName = it
                    },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.hint_new_notebook),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedPlaceholderColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onPrimary,
                        focusedTextColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium,
                )
            },
            dismissButton = {
                TextButton(onClick = {
                    onCancel()
                }) {
                    Text(stringResource(id = android.R.string.cancel))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onNewNotebook(notebookName)
                    notebookName = ""
                }) {
                    Text(stringResource(id = android.R.string.ok))
                }
            }
        )
    }
}

@Composable
fun DeletionConfirmDialog(showDialog: Boolean,
                          onCancel: () -> Unit,
                          onConfirmed: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
            },

            title = {
                Text(
                    text = stringResource(id = R.string.label_delete),
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    text = stringResource(id = R.string.prompt_deletion),
                    style = MaterialTheme.typography.bodyMedium

                )

            },
            dismissButton = {
                TextButton(onClick = {
                    onCancel()
                }) {
                    Text(stringResource(id = android.R.string.cancel))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onConfirmed()
                }) {
                    Text(stringResource(id = android.R.string.ok))
                }
            }
        )
    }
}
