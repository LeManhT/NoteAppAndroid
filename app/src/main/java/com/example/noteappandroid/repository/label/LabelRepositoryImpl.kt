package com.example.noteappandroid.repository.label

import android.util.Log
import com.example.noteappandroid.databases.dao.LabelDao
import com.example.noteappandroid.entity.Label
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LabelRepositoryImpl @Inject constructor(private val labelDao: LabelDao) : LabelRepository {

    override fun getAllLabels(): Flow<List<Label>> {
        return labelDao.getAllLabels()
    }

    override fun getLabelsByFolderId(folderId: Long): Flow<List<Label>> {
        return labelDao.getLabelsByFolderId(folderId)
    }

    override fun getLabelById(id: Long): Flow<Label> {
        return labelDao.getLabelById(id)
    }

    override suspend fun createLabel(label: Label): Long {
        return labelDao.createLabel(label)
    }

    override suspend fun updateLabel(label: Label) {
        labelDao.updateLabel(label)
    }

    override suspend fun deleteLabel(label: Label) {
        labelDao.deleteLabel(label)
    }

    override fun getLabelsForNoteId(noteId: Long): Flow<List<Label>> {
//        Log.d("TAG", "getLabelsForNoteId called with noteId: $noteId")
        return labelDao.getLabelsForNoteId(noteId)
    }
}