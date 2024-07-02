package com.example.noteappandroid.repository.note_labels

import com.example.noteappandroid.entity.NoteLabel
import kotlinx.coroutines.flow.Flow

interface NoteLabelRepository {
    fun getNoteLabelsByNoteId(noteId: Long): Flow<List<NoteLabel>>
    fun getNoteLabels(): Flow<List<NoteLabel>>
    suspend fun createNoteLabel(noteLabel: NoteLabel)
    suspend fun deleteNoteLabel(noteId: Long, labelId: Long)
}
