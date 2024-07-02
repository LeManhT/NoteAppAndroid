package com.example.noteappandroid.databases.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.noteappandroid.entity.Folder
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao {

    @Query("SELECT * FROM folders")
    fun getAllFolders(): Flow<List<Folder>>

    @Query("SELECT * FROM folders WHERE parent_id IS -1")
    fun getRootFolders(): Flow<List<Folder>>

    @Query("SELECT * FROM folders WHERE parent_id = :parentId")
    fun getSubFolders(parentId: Long): Flow<List<Folder>>


    @Query("SELECT * FROM folders WHERE id = :folderId")
    fun getFolderById(folderId: Long): Flow<Folder>

    @Insert
    suspend fun createFolder(folder: Folder): Long

    @Update
    suspend fun updateFolder(folder: Folder)

    @Delete
    suspend fun deleteFolder(folder: Folder)

    @Query("DELETE FROM folders")
    suspend fun clearFolders()
}