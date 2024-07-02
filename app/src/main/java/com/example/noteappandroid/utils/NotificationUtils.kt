package com.example.noteappandroid.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.noteappandroid.MainActivity
import com.example.noteappandroid.R
import com.example.noteappandroid.entity.Folder
import com.example.noteappandroid.entity.NoteEntity

private const val RemindersChannelId = "Reminders"

fun NotificationManager.sendReminderNotification(
    context: Context,
    folder: Folder?,
    note: NoteEntity,
) {
    val pendingIntent = note.folderId?.let { context.createNotificationPendingIntent(note.id, it) }

    val style = NotificationCompat.BigTextStyle()
        .bigText(note.noteText?.ifBlank { note.title })

    val notification = folder?.color?.toColorResourceId()?.let { context.colorResource(it) }?.let {
        NotificationCompat.Builder(context, RemindersChannelId)
        .setContentTitle(note.title?.ifBlank { note.noteText })
        .setContentText(note.noteText?.ifBlank { note.title })
        .setContentIntent(pendingIntent)
//        .setSubText(folder.getTitle(context))
        .setStyle(style)
        .setColor(it)
        .setColorized(true)
        .setCategory(Notification.CATEGORY_REMINDER)
        .setSmallIcon(R.drawable.ic_round_notifications_active_24)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .setGroupSummary(true)
        .build()
    }

    notify("folder.getTitle(context)", note.id.toInt(), notification)
}

fun NotificationManager.createNotificationChannels(context: Context) {
    NotificationChannel(
        RemindersChannelId,
        context.stringResource(R.string.reminder),
        NotificationManager.IMPORTANCE_HIGH
    )
        .apply {
            enableVibration(false)
            enableLights(false)
            setSound(null, null)
        }
        .also(this::createNotificationChannel)
}

fun Context.createNotificationPendingIntent(noteId: Long, folderId: Long): PendingIntent {
    val intent = Intent(this, MainActivity::class.java).apply {
        putExtra("NOTE_ID", noteId)
        putExtra("FOLDER_ID", folderId)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    return PendingIntent.getActivity(
        this,
        noteId.toInt(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}

