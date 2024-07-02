package com.example.noteappandroid.databases.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.noteappandroid.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteAppDao {

    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getSpecificNote(id: Long): NoteEntity

    @Query("SELECT * FROM Notes WHERE folder_id = :folderId")
    fun getNotesInFolder(folderId: Long): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(note: NoteEntity)

    @Delete
    suspend fun deleteNotes(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteSpecificNote(id: Long)

    @Update
    suspend fun updateNotes(note: NoteEntity)

    @Query(
        """
    SELECT DISTINCT Notes.* FROM Notes 
    LEFT JOIN note_labels ON Notes.id = note_labels.note_id
    WHERE Notes.folder_id = :folderId
    AND (:labelId IS NULL OR note_labels.label_id = :labelId)
    AND (Notes.title LIKE '%' || :searchQuery || '%' OR Notes.note_text LIKE '%' || :searchQuery || '%') 
    ORDER BY Notes.date_time DESC
"""
    )
    fun getNotesInFolderWithSearchAndLabel(
        folderId: Long,
        searchQuery: String,
        labelId: Long?
    ): Flow<List<NoteEntity>>
}