package com.example.noteappandroid.ui.folder

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteappandroid.entity.Folder
import com.example.noteappandroid.repository.folder.FolderRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

import javax.inject.Inject

@HiltViewModel
class FolderViewModel @Inject constructor(
    private val folderRepositoryImpl: FolderRepositoryImpl,
) :
    ViewModel() {

//    init {
//        getAllFolders()
//    }

    private val _folders = MutableStateFlow<List<Folder>>(emptyList())
    val folders: StateFlow<List<Folder>> = _folders.asStateFlow()

    private val _listSubFolders = MutableStateFlow<List<Folder>>(emptyList())
    val listSubFolders: StateFlow<List<Folder>> = _listSubFolders.asStateFlow()

    private val _folder = MutableStateFlow<Folder?>(null)
    val folder: StateFlow<Folder?> = _folder.asStateFlow()

    fun getAllFolders() {
        viewModelScope.launch {
            folderRepositoryImpl.getAllFolders()
                .catch { e -> Log.e("FolderViewModel", "getAllFolders: ${e.message}") }
                .collect { folderList ->
                    _folders.value = folderList
                }
        }
    }

    fun getRootFolders() {
        viewModelScope.launch {
            folderRepositoryImpl.getRootFolders()
                .catch { e -> Log.e("FolderViewModel", "getRootFolders: ${e.message}") }
                .collect { folderList ->
                    _folders.value = folderList
                }
        }
    }

    fun getSubFolders(parentId: Long) {
        viewModelScope.launch {
            folderRepositoryImpl.getSubFolders(parentId)
                .catch { e -> Log.e("FolderViewModel", "getSubFolders: ${e.message}") }
                .collect { folderList ->
                    _listSubFolders.value = folderList
                }
        }
    }

    fun getFolderById(folderId: Long) {
        viewModelScope.launch {
            folderRepositoryImpl.getFolderById(folderId)
                .catch { e -> /* Handle error here */ }
                .collect { folder ->
                    _folder.value = folder
                }
        }
    }

    fun createFolder(folder: Folder) {
        viewModelScope.launch {
            folderRepositoryImpl.createFolder(folder)
            getAllFolders() // Refresh the folder list after creation
        }
    }

    fun updateFolder(folder: Folder) {
        viewModelScope.launch {
            folderRepositoryImpl.updateFolder(folder)
            getAllFolders()
        }
    }

    fun deleteFolder(folder: Folder) {
        viewModelScope.launch {
            folderRepositoryImpl.deleteFolder(folder)
            getAllFolders()
        }
    }

    fun clearFolders() {
        viewModelScope.launch {
            folderRepositoryImpl.clearFolders()
            getAllFolders()
        }
    }

}