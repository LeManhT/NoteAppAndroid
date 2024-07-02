package com.example.noteappandroid.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.AlarmManagerCompat
import com.example.noteappandroid.receiver.NoteReminderReceiver

fun AlarmManager.createAlarm(
    context: Context,
    folderId: Long?,
    noteId: Long,
    epochMilliseconds: Long
) {

    val intent = Intent(context, NoteReminderReceiver::class.java).apply {
        putExtra(Constants.FolderId, folderId)
        putExtra(Constants.NoteId, noteId)
    }

    val pendingIntent =
        PendingIntent.getBroadcast(context, noteId.toInt(), intent, PendingIntentFlags)
    Log.d("TAGGGG","NoteId : $noteId")
    AlarmManagerCompat.setExactAndAllowWhileIdle(
        this,
        AlarmManager.RTC_WAKEUP,
        epochMilliseconds,
        pendingIntent
    )
}

fun AlarmManager.cancelAlarm(context: Context, noteId: Long) {

    val intent = Intent(context, NoteReminderReceiver::class.java)
    val pendingIntent =
        PendingIntent.getBroadcast(context, noteId.toInt(), intent, PendingIntentFlags)

    cancel(pendingIntent)
}