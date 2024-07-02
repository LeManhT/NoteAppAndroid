package com.example.noteappandroid.repository.label

import com.example.noteappandroid.entity.Label
import kotlinx.coroutines.flow.Flow

interface LabelRepository {
    fun getAllLabels(): Flow<List<Label>>
    fun getLabelsByFolderId(folderId: Long): Flow<List<Label>>
    fun getLabelById(id: Long): Flow<Label>
    fun getLabelsForNoteId(noteId: Long): Flow<List<Label>>
    suspend fun createLabel(label: Label): Long
    suspend fun updateLabel(label: Label)
    suspend fun deleteLabel(label: Label)
}