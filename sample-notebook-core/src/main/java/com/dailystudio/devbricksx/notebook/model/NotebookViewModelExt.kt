package com.dailystudio.devbricksx.notebook.model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.notebook.db.Note
import com.dailystudio.devbricksx.notebook.db.Notebook
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class NotebookViewModelExt(application: Application): NotebookViewModel(application) {

    val notebooks: Flow<PagingData<Notebook>> =
        Pager(
            PagingConfig(20)
        ) {
            notebookRepository.getAllNotebooksOrderedByLastModifiedPagingSource()
        }.flow.flowOn(Dispatchers.IO)


    private val _currentNoteId = MutableStateFlow<Int>(-1)
    private val _currentNotebookId = MutableStateFlow<Int>(-1)

    val currentNote: LiveData<Note?> =
        _currentNoteId.flatMapLatest { noteId ->
            Logger.debug("[DC] retrieve note: ${noteId}")

            noteId?.let {
                noteRepository.getNoteFlow(noteId)
            } ?: flow {
                emit(null)
            }
        }.flowOn(Dispatchers.IO).asLiveData() ?: MutableLiveData(null)


    val notesInOpenedNotebook: Flow<PagingData<Note>> =
        _currentNotebookId.flatMapLatest { notebookId ->
            Pager (
                PagingConfig(20),
            ) {
                getAllNotesOrderedByLastModifiedLivePaged(notebookId)
            }.flow.flowOn(Dispatchers.IO).cachedIn(viewModelScope)
        }.flowOn(Dispatchers.IO)


    private val _currentNote: MutableLiveData<Note> =
        MutableLiveData(Note.createNote())

    fun openNotebook(notebookId: Int) {
        _currentNotebookId.value = notebookId
    }

    fun closeNotebook() {
        _currentNotebookId.value = -1
    }

    fun openNote(id: Int) {
        _currentNoteId.value = id
    }

    fun closeNote() {
        _currentNoteId.value  = -1
    }

    fun deleteNotes(ids: Set<Int>): Job =
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.deleteNotes(ids.toIntArray())
        }

    fun deleteNotebooks(ids: Set<Int>): Job =
        viewModelScope.launch(Dispatchers.IO) {
            notebookRepository.deleteNotebooks(ids.toIntArray())
        }

}