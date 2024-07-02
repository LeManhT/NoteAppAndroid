package com.example.noteappandroid.databases.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.noteappandroid.entity.NoteLabel
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteLabelDao {
    @Query("SELECT * FROM note_labels WHERE note_id = :noteId")
    fun getNoteLabelsByNoteId(noteId: Long): Flow<List<NoteLabel>>

    @Query("SELECT * FROM note_labels")
    fun getNoteLabels(): Flow<List<NoteLabel>>

    @Insert
    suspend fun createNoteLabel(noteLabel: NoteLabel)

    @Query("DELETE FROM note_labels WHERE note_id = :noteId AND label_id = :labelId")
    suspend fun deleteNoteLabel(noteId: Long, labelId: Long)
}