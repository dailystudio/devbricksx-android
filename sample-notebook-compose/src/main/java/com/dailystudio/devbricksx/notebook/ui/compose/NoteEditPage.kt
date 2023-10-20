package com.dailystudio.devbricksx.notebook.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.notebook.core.R
import com.dailystudio.devbricksx.notebook.db.Note

const val MENU_ITEM_COMPLETED = 0x1

@Composable
fun NoteEditPage(note: Note,
                 onEditCompleted: (Note) -> Unit) {
    Logger.debug("edit note: $note")

    var noteTitle by remember(note.id) { mutableStateOf(note.title) }
    var noteContent by remember(note.id) { mutableStateOf(note.desc) }

    Scaffold(
        topBar = {
            NoteEditTopAppBar(
                onMenuItemClick = {
                    val updatedNote = Note.copyNote(note).apply {
                        title = noteTitle
                        desc = noteContent
                    }

                    Logger.debug("complete edit: $updatedNote")
                    onEditCompleted(updatedNote)
                }
            )
        }
    ) {padding ->
        Logger.debug("padding: $padding")

        NoteEdit(
            title = noteTitle,
            onTitleChanged = { noteTitle = it },
            content = noteContent,
            onContentChanged = { noteContent = it },
            Modifier
                .padding(padding)
                .background(Color.White),
        )
    }
}

@Composable
fun NoteEdit(title: String?,
             onTitleChanged: (String) -> Unit,
             content: String?,
             onContentChanged: (String) -> Unit,
             modifier: Modifier = Modifier
) {
    Logger.debug("edit title: $title")
    Logger.debug("edit content: $content")
    val focusRequester = remember { FocusRequester() }

    Column(modifier.padding(16.dp)) {
        TextField(
            value = title ?: "",
            onValueChange = onTitleChanged,
            placeholder = {
                Text(
                    stringResource(id = R.string.hint_title),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
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
            textStyle = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Divider()

        val selection = if (content.isNullOrEmpty()) {
            TextRange.Zero
        } else {
            TextRange(content.length, content.length)
        }

        TextField(
            value = content ?: "",
            onValueChange = onContentChanged,
            placeholder = {
                Text(
                    text = stringResource(id = R.string.hint_content),
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
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester)
        )

        DisposableEffect(Unit) {
            focusRequester.requestFocus()
            onDispose { }
        }

    }
}