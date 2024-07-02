package com.example.noteappandroid.repository.note_labels

import com.example.noteappandroid.databases.dao.NoteLabelDao
import com.example.noteappandroid.entity.NoteLabel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteLabelRepositoryImpl @Inject constructor(private val noteLabelDao: NoteLabelDao) :
    NoteLabelRepository {
    override fun getNoteLabelsByNoteId(noteId: Long): Flow<List<NoteLabel>> {
        return noteLabelDao.getNoteLabelsByNoteId(noteId)
    }

    override fun getNoteLabels(): Flow<List<NoteLabel>> {
        return noteLabelDao.getNoteLabels()
    }

    override suspend fun createNoteLabel(noteLabel: NoteLabel) {
        noteLabelDao.createNoteLabel(noteLabel)
    }

    override suspend fun deleteNoteLabel(noteId: Long, labelId: Long) {
        noteLabelDao.deleteNoteLabel(noteId, labelId)
    }
}
