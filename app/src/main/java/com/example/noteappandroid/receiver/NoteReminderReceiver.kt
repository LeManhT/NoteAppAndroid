package com.example.noteappandroid.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.noteappandroid.repository.folder.FolderRepositoryImpl
import com.example.noteappandroid.repository.notes.NoteRepositoryImple
import com.example.noteappandroid.utils.Constants
import com.example.noteappandroid.utils.sendReminderNotification
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class NoteReminderReceiver : BroadcastReceiver() {
    @Inject
    lateinit var folderRepository: FolderRepositoryImpl

    @Inject
    lateinit var noteRepository: NoteRepositoryImple

    override fun onReceive(context: Context?, intent: Intent?) {

        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        intent?.let {
            val folderId = it.getLongExtra(Constants.FolderId, 0)
            val noteId = it.getLongExtra(Constants.NoteId, 0)

            runBlocking {
                val folder = folderRepository.getFolderById(folderId)
                    .firstOrNull()
                val note = noteRepository.getNote(noteId)

                Log.d("TAGGGG","Receiver Intent : $noteId")
//                if (folder != null && context != null) {
                if (context != null) {
                    notificationManager?.sendReminderNotification(context, folder, note)
                }
                    noteRepository.updateNotes(note.copy(reminderDate = null))
//                }
            }
        }
    }
}