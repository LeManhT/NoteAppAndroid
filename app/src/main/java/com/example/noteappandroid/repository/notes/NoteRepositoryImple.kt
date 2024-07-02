package com.example.noteappandroid.repository.notes

import com.example.noteappandroid.databases.dao.NoteAppDao
import com.example.noteappandroid.entity.NoteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NoteRepositoryImple @Inject constructor(private val notesDao: NoteAppDao) : NoteRepository {
    val notes = notesDao.getAllNotes()

    override fun getAllNotes(): kotlinx.coroutines.flow.Flow<List<NoteEntity>> {
        return notesDao.getAllNotes()
    }

    override suspend fun getNote(id: Long): NoteEntity = withContext(Dispatchers.IO) {
        notesDao.getSpecificNote(id)
    }

    override suspend fun insertNote(note: NoteEntity) = withContext(Dispatchers.IO) {
        notesDao.insertNotes(note)
    }

    override suspend fun deleteNote(note: NoteEntity) = withContext(Dispatchers.IO) {
        notesDao.deleteNotes(note)
    }

    override suspend fun deleteNoteById(id: Long) = withContext(Dispatchers.IO) {
        notesDao.deleteSpecificNote(id)
    }

    override suspend fun updateNotes(note: NoteEntity) = withContext(Dispatchers.IO) {
        notesDao.updateNotes(note)
    }

    override fun getNotesInFolder(folderId: Long): Flow<List<NoteEntity>> {
        return notesDao.getNotesInFolder(folderId)
    }

    override fun getNotesInFolderWithSearchAndLabel(
        folderId: Long,
        searchQuery: String,
        labelId: Long?
    ): Flow<List<NoteEntity>> {
        return notesDao.getNotesInFolderWithSearchAndLabel(folderId, searchQuery, labelId)
    }
}