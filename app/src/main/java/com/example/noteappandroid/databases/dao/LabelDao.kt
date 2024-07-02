package com.example.noteappandroid.databases.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.noteappandroid.entity.Label
import kotlinx.coroutines.flow.Flow
@Dao
interface LabelDao {
    @Query("SELECT * FROM labels")
    fun getAllLabels(): Flow<List<Label>>

    @Query("SELECT * FROM labels WHERE folder_id = :folderId")
    fun getLabelsByFolderId(folderId: Long): Flow<List<Label>>

    @Query("SELECT * FROM labels WHERE id = :id")
    fun getLabelById(id: Long): Flow<Label>

    @Query("""
        SELECT labels.* FROM labels
        INNER JOIN note_labels ON labels.id = note_labels.label_id
        WHERE note_labels.note_id = :noteId
    """)
    fun getLabelsForNoteId(noteId: Long): Flow<List<Label>>

    @Insert
    suspend fun createLabel(label: Label): Long

    @Update
    suspend fun updateLabel(label: Label)

    @Delete
    suspend fun deleteLabel(label: Label)
}