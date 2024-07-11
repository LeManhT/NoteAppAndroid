package com.example.noteappandroid.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.noteappandroid.entity.Folder
import com.example.noteappandroid.entity.NoteEntity
import com.example.noteappandroid.utils.Constants
import com.example.noteappandroid.utils.sendReminderNotification
import kotlinx.coroutines.runBlocking

class NoteReminderReceiver :
    BroadcastReceiver() {
    //    @Inject //    lateinit var folderRepository: FolderRepositoryImpl
    //    @Inject //    lateinit var noteRepository: NoteRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        intent?.let {
            val folderId = it.getLongExtra(Constants.FolderId, 0)
            val noteId = it.getLongExtra(
                Constants.NoteId,
                0
            )
            runBlocking {
                //                val folder = folderRepository.getFolderById(folderId) //
                //                .firstOrNull() //
                //                val note = noteRepository.getNote(noteId)
                //                if (folder != null && context != null) {
                if (context != null) {
                    notificationManager?.sendReminderNotification(
                        context, Folder(), NoteEntity(0, "Set reminder for note successfully !", 1)
                    )
                }
                //                noteRepository.updateNotes(note.copy(reminderDate = null)) //
            }
        }
    }
}

