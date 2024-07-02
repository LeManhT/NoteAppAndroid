package com.example.noteappandroid.repository.folder

import com.example.noteappandroid.entity.Folder
import kotlinx.coroutines.flow.Flow

interface FolderRepository {
    fun getAllFolders(): Flow<List<Folder>>

    fun getFolderById(folderId: Long): Flow<Folder>

    suspend fun createFolder(folder: Folder): Long

    suspend fun updateFolder(folder: Folder)

    suspend fun deleteFolder(folder: Folder)

    suspend fun clearFolders()

    fun getRootFolders(): Flow<List<Folder>>

    fun getSubFolders(parentId: Long): Flow<List<Folder>>

}