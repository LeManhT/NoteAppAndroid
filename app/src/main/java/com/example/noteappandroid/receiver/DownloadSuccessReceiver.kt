package com.example.noteappandroid.receiver

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import com.example.noteappandroid.R
import java.io.File

class DownloadSuccessReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(
            "Broadcast",
            "onReceive : $intent"
        )
        if (intent?.action == "PDF_DOWNLOAD_COMPLETE") {
            val filePath =
                intent.getStringExtra("file_path")
            Log.d("Broadcast", "Vào $filePath")
            showDownloadCompleteNotification(context, filePath)
        }
    }

    private fun showDownloadCompleteNotification(context: Context?, filePath: String?) {
        if (context == null || filePath == null) {
            return
        }
        Log.d("Broadcast", "Show $filePath")
        val intent = Intent(Intent.ACTION_VIEW)
        val file = File(filePath)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        intent.setDataAndType(uri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val channelId =
            "download_channel" // Make sure channelId is defined
        val notificationBuilder =
            NotificationCompat.Builder(context, channelId).setContentTitle("Download Complete")
                .setContentText("File download is complete. Click to open.")
                .setSmallIcon(R.drawable.download).setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent).setAutoCancel(true)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(3, notificationBuilder.build())
    }
}
