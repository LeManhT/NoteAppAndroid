package com.example.noteappandroid.repository.notes

import com.example.noteappandroid.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    suspend fun getNote(id: Long): NoteEntity
    suspend fun insertNote(note: NoteEntity)
    suspend fun deleteNote(note: NoteEntity)
    suspend fun updateNotes(note: NoteEntity)
    fun getAllNotes(): Flow<List<NoteEntity>>
    fun getNotesInFolder(folderId: Long): Flow<List<NoteEntity>>
    suspend fun deleteNoteById(id: Long)
    fun getNotesInFolderWithSearchAndLabel(folderId: Long, searchQuery: String, labelId: Long?): Flow<List<NoteEntity>>
}