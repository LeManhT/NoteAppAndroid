package com.example.noteappandroid.repository.folder

import android.util.Log
import com.example.noteappandroid.databases.dao.FolderDao
import com.example.noteappandroid.entity.Folder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FolderRepositoryImpl @Inject constructor(private val folderDao: FolderDao) :
    FolderRepository {
    override fun getAllFolders(): Flow<List<Folder>> {
        return folderDao.getAllFolders()
    }

    override fun getFolderById(folderId: Long): Flow<Folder> {
        Log.d("TAG", "getFolderById: $folderId")
        return folderDao.getFolderById(folderId)
    }

    override suspend fun createFolder(folder: Folder): Long {
        return folderDao.createFolder(folder)
    }

    override suspend fun updateFolder(folder: Folder) {
        folderDao.updateFolder(folder)
    }

    override suspend fun deleteFolder(folder: Folder) {
        folderDao.deleteFolder(folder)
    }

    override suspend fun clearFolders() {
        folderDao.clearFolders()
    }

    override fun getRootFolders(): Flow<List<Folder>> {
        return folderDao.getRootFolders()
    }

    override fun getSubFolders(parentId: Long): Flow<List<Folder>> {
        return folderDao.getSubFolders(parentId)
    }
}