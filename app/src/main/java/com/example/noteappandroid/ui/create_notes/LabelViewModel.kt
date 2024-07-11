package com.example.noteappandroid.ui.create_notes

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteappandroid.entity.Label
import com.example.noteappandroid.repository.label.LabelRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LabelViewModel @Inject constructor(private val labelRepositoryImpl: LabelRepositoryImpl) :
    ViewModel() {
    private val _labels = MutableStateFlow<List<Label>>(emptyList())
    val labels = _labels.asStateFlow()

    private val _label = MutableStateFlow<Label?>(null)
    val label = _label.asStateFlow()

    private val _labelsForNoteId = MutableStateFlow<List<Label>>(emptyList())
    val labelsForNoteId = _labelsForNoteId.asStateFlow()

    private val _newLabelId = MutableStateFlow<Long>(-1)
    val newLabelId = _newLabelId.asStateFlow()

    private val _labelsForFolderId = MutableStateFlow<List<Label>>(emptyList())
    val labelsForFolderId = _labelsForFolderId.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    init {
        getAllLabels()
    }

    private fun getAllLabels() {
        viewModelScope.launch {
            labelRepositoryImpl.getAllLabels().collect { labels ->
                _labels.emit(labels)
            }
        }
    }

    fun getLabelsByFolderId(folderId: Long) {
        viewModelScope.launch {
            labelRepositoryImpl.getLabelsByFolderId(folderId).collect { labels ->
                _labelsForFolderId.emit(labels)
            }
        }
    }

    fun getLabelsForNoteId(noteId: Long) {
        viewModelScope.launch {
            labelRepositoryImpl.getLabelsForNoteId(noteId).collect { labels ->
                _labelsForNoteId.emit(labels)
            }
        }
    }

    fun createLabel(label: Label) {
        viewModelScope.launch {
            try {
                val newLabelId = labelRepositoryImpl.createLabel(label)
                Log.d("TAGCR", "createLabel: $newLabelId")
                _newLabelId.emit(newLabelId)
            } catch (e: SQLiteConstraintException) {
                _errorMessage.emit("Label title already exists")
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun updateLabel(label: Label) {
        viewModelScope.launch {
            labelRepositoryImpl.updateLabel(label)
        }
    }

    fun deleteLabel(label: Label) {
        viewModelScope.launch {
            labelRepositoryImpl.deleteLabel(label)
        }
    }
}