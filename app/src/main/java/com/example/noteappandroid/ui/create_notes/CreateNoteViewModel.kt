package com.example.noteappandroid.ui.create_notes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteappandroid.entity.Folder
import com.example.noteappandroid.entity.NoteEntity
import com.example.noteappandroid.repository.folder.FolderRepositoryImpl
import com.example.noteappandroid.repository.notes.NoteRepositoryImple
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import kotlin.time.Duration.Companion.days

private val ExtraDatePeriod = 1.days

@HiltViewModel
class CreateNoteViewModel @Inject constructor(
    private val noteRepositoryImple: NoteRepositoryImple,
    private val folderRepositoryImpl: FolderRepositoryImpl,
) :
    ViewModel() {

    private val mutableReminderDateTime =
        MutableStateFlow(kotlinx.datetime.Clock.System.now().plus(ExtraDatePeriod))
    val reminderDateTime get() = mutableReminderDateTime.asStateFlow()

    val noteId = MutableStateFlow<Long?>(null)
    fun setNoteId(id: Long) = viewModelScope.launch {
        noteId.emit(id)
        if (id != -1L) {
            noteRepositoryImple.getNote(id).let { note ->
                Log.d("TAG", "setNoteId: ${Gson().toJson(note)}")
                val currentDateTime =
                    note.reminderDate ?: kotlinx.datetime.Clock.System.now().plus(ExtraDatePeriod)
                mutableReminderDateTime.value = currentDateTime
            }
        }
    }

    val folder = folderRepositoryImpl.getFolderById(1)
        .filterNotNull()
        .stateIn(viewModelScope, SharingStarted.Lazily, Folder())

    @OptIn(ExperimentalCoroutinesApi::class)
    val note = noteId.flatMapLatest {
        val note = it?.let {
            noteRepositoryImple.getNote(it)
        }
        flowOf(note)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _folderId = MutableStateFlow<Long?>(null)

    fun setFolderId(id: Long) {
        _folderId.value = id
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val notesInFolder = _folderId
        .filterNotNull()
        .flatMapLatest { folderId ->
            noteRepositoryImple.getNotesInFolder(folderId)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val searchQuery = MutableStateFlow("")
    private val selectedLabelId = MutableStateFlow<Long?>(null)

    fun onSearchQueryChanged(query: String) = viewModelScope.launch {
        searchQuery.emit(query)
    }

    fun onLabelSelected(labelId: Long?) {
        selectedLabelId.value = labelId
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val notesInFolderWithSearchAndLabel = combine(
        _folderId.filterNotNull(),
        searchQuery,
        selectedLabelId
    ) { folderId, query, labelId ->
        Triple(folderId, query, labelId)
    }.flatMapLatest { (folderId, query, labelId) ->
        noteRepositoryImple.getNotesInFolderWithSearchAndLabel(folderId, query, labelId)
    }.distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    suspend fun updateNote(noteEntity: NoteEntity) = noteRepositoryImple.updateNotes(noteEntity)

    suspend fun saveNote(noteEntity: NoteEntity) = noteRepositoryImple.insertNote(noteEntity)

    suspend fun deleteNote() = noteId.value?.let { noteRepositoryImple.deleteNoteById(it) }

    fun setReminderDate(epochMilliseconds: Long) {
        val currentDateTime =
            reminderDateTime.value.toLocalDateTime(TimeZone.currentSystemDefault())
        val updatedDateTime = kotlinx.datetime.Instant.fromEpochMilliseconds(epochMilliseconds)
            .toLocalDateTime(TimeZone.UTC).let {
                kotlinx.datetime.LocalDateTime(
                    it.year,
                    it.monthNumber,
                    it.dayOfMonth,
                    currentDateTime.hour,
                    currentDateTime.minute,
                    it.second,
                    it.nanosecond
                )
            }
        mutableReminderDateTime.value = updatedDateTime.toInstant(TimeZone.currentSystemDefault())
    }

    fun setReminderTime(hour: Int, minute: Int) {
        val currentDateTime =
            reminderDateTime.value.toLocalDateTime(TimeZone.currentSystemDefault())
        val updatedDateTime = currentDateTime.let {
            kotlinx.datetime.LocalDateTime(
                it.year,
                it.monthNumber,
                it.dayOfMonth,
                hour,
                minute,
                it.second,
                it.nanosecond
            )
        }
        mutableReminderDateTime.value = updatedDateTime.toInstant(TimeZone.currentSystemDefault())
    }

    fun setNoteReminder() = viewModelScope.launch {
        Log.d("TAG", "setNoteReminder1: ${Gson().toJson(noteId.value)}")
        Log.d("TAG", "setNoteReminder: ${Gson().toJson(note.value)}")
        note.value?.let { noteRepositoryImple.updateNotes(it.copy(reminderDate = reminderDateTime.value)) }
    }

    fun cancelNoteReminder() = viewModelScope.launch {
        note.value?.copy(reminderDate = null)?.let { noteRepositoryImple.updateNotes(it) }
    }

}