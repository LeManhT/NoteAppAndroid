package com.example.noteappandroid.listeners

import com.example.noteappandroid.entity.Folder

interface FolderClickListener {
    fun handleFolderClick(folder: Folder)
}