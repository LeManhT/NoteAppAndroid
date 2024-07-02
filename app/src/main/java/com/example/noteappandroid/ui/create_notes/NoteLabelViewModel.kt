package com.example.noteappandroid.ui.create_notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteappandroid.entity.NoteLabel
import com.example.noteappandroid.repository.note_labels.NoteLabelRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteLabelViewModel @Inject constructor(private val noteLabelRepositoryImpl: NoteLabelRepositoryImpl) :
    ViewModel() {
    private val _noteLabels = MutableStateFlow<List<NoteLabel>>(emptyList())
    val noteLabels = _noteLabels.asStateFlow()

    private val _noteLabelByNoteId = MutableStateFlow<List<NoteLabel?>>(emptyList())
    val noteLabelsByNoteId = _noteLabelByNoteId.asStateFlow()
//
//    init {
//        getAllNoteLabels()
//    }

    private fun getAllNoteLabels() {
        viewModelScope.launch {
            noteLabelRepositoryImpl.getNoteLabels().collect { labels ->
                _noteLabels.emit(labels)
            }
        }
    }

    fun getNoteLabelsByNoteId(noteId: Long) {
        viewModelScope.launch {
            noteLabelRepositoryImpl.getNoteLabelsByNoteId(noteId).collect { labels ->
                _noteLabelByNoteId.emit(labels)
            }
        }
    }

    fun createNoteLabel(noteLabel: NoteLabel) {
        viewModelScope.launch {
            noteLabelRepositoryImpl.createNoteLabel(noteLabel)
        }
    }

    fun deleteNoteLabel(noteId: Long, folderId: Long) {
        viewModelScope.launch {
            noteLabelRepositoryImpl.deleteNoteLabel(noteId,folderId)
        }
    }

}