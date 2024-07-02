package com.example.noteappandroid.ui.create_notes

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.noteappandroid.R
import com.example.noteappandroid.adapter.FolderAdapter
import com.example.noteappandroid.base.BaseDialogFragment
import com.example.noteappandroid.databinding.FragmentNoteDialogBinding
import com.example.noteappandroid.entity.Folder
import com.example.noteappandroid.entity.NoteEntity
import com.example.noteappandroid.listeners.FolderClickListener
import com.example.noteappandroid.ui.folder.FolderViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

class NoteDialogFragment : BaseDialogFragment(), FolderClickListener {
    private lateinit var binding: FragmentNoteDialogBinding
    private val noteViewModel by viewModels<CreateNoteViewModel>()
    private val args: NoteDialogFragmentArgs by navArgs()

    private val folderViewModel by viewModels<FolderViewModel>()
    private lateinit var adapter: FolderAdapter
    private var folderId: Long = 0
    private var layoutNone: MaterialTextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        noteId = args.noteId
        noteViewModel.setNoteId(args.noteId)
        folderViewModel.getAllFolders()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            noteViewModel.note.collect {
                binding.vNote.tvNoteTitle.text = it?.title
                if (it != null) {
                    val html = it.noteText
                    val trimmedHtml = html?.trim()
                    val spannable = Html.fromHtml(trimmedHtml, Html.FROM_HTML_MODE_LEGACY)
                    binding.vNote.tvNoteBody.text = spannable
                }
//                binding.vNote.tvNoteBody.text = it?.noteText
                if (it?.reminderDate != null) {
                    binding.vNote.tvReminder.text = it.reminderDate.toString()
                }
                binding.vNote.tvCreationDate.text = it?.dateTime
            }
        }

        binding.tvShareNote.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, noteViewModel.note.value?.noteText.toString())
            val chooser = Intent.createChooser(shareIntent, getString(R.string.share_note_via))
            startActivity(chooser)
        }

        binding.tvDeleteNote.setOnClickListener {
            context?.let { it1 ->
                MaterialAlertDialogBuilder(it1).setTitle(resources.getString(R.string.title))
                    .setMessage(resources.getString(R.string.are_you_sure_to_delete_the_note))
                    .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                        dialog.dismiss()
                    }.setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                        viewLifecycleOwner.lifecycleScope.launch {
                            noteViewModel.deleteNote()
                            dismiss()
                        }
                    }.show()
            }
        }

        binding.tvExportNote.setOnClickListener {
            showSaveFormatDialog()
        }

        binding.tvMoveNote.setOnClickListener {
            showDetailDialog()
        }

        binding.tvRemindMe.setOnClickListener {
            val action = noteViewModel.note.value?.id?.let { it1 ->
                NoteDialogFragmentDirections.actionNoteDialogFragmentToNoteReminderDialog(
                    1, it1
                )
            }

            if (action != null) {
                findNavController().navigate(action)
            }
        }

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        disableSelection()
    }

    @SuppressLint("SetTextI18n")
    private fun showDetailDialog() {
        val dialogChooseParent = context?.let { Dialog(it) }
        dialogChooseParent?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogChooseParent?.setContentView(R.layout.choose_parent_dialog)
        val window = dialogChooseParent?.window ?: return
        window.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutNone = dialogChooseParent.findViewById(R.id.tv_parent_folder_option)
//        window.setBackgroundDrawableResource(R.drawable.rounded_corner)
        layoutNone?.setOnClickListener {
            adapter.setRecyclerViewPosition(RecyclerView.NO_POSITION)
            layoutNone?.setBackgroundColor(Color.LTGRAY)
            folderId = -1
        }

        val rcv = dialogChooseParent.findViewById<RecyclerView>(R.id.rvListFolders)
        adapter = FolderAdapter(this)
        rcv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rcv.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launch {
            folderViewModel.folders.collect { folderList ->
                Log.d("TAG", "showDetailDialog: $folderList")
                adapter.updateData(folderList)
            }
        }
        dialogChooseParent.findViewById<ImageView>(R.id.btnClose).setOnClickListener {
            dialogChooseParent.dismiss()
        }
        dialogChooseParent.findViewById<MaterialButton>(R.id.btnChoose).setOnClickListener {
            val selectedFolder = adapter.getSelectedFolder()
            if (selectedFolder != null) {
                folderId = selectedFolder.id
            } else {
                folderId = 1
            }
            Log.d("TAG", "showDetailDialog: $folderId")
            viewLifecycleOwner.lifecycleScope.launch {
                noteViewModel.note.value.let {
                    it?.folderId = folderId
                    if (it != null) {
                        noteViewModel.updateNote(it)
                    }
                }
            }
            dialogChooseParent.dismiss()
        }
        dialogChooseParent.show()
    }

    override fun handleFolderClick(folder: Folder) {
        layoutNone?.setBackgroundColor(Color.WHITE)
    }

    private fun showSaveFormatDialog() {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.fragment_download_dialog, null)

        val btnDownloadExcel = dialogView.findViewById<LinearLayout>(R.id.btnDownloadExcel)
        val btnDownloadWord = dialogView.findViewById<LinearLayout>(R.id.btnDownloadWord)

        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        val dialog = builder.create()
        btnDownloadExcel.setOnClickListener {
            lifecycleScope.launch { saveExcel(noteViewModel.note.value!!) }
            dialog.dismiss()
        }

        btnDownloadWord.setOnClickListener {
            lifecycleScope.launch { saveDocx(noteViewModel.note.value!!) }
            dialog.dismiss()
        }

        dialog.show()
    }


    private suspend fun saveDocx(note: NoteEntity) {
        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = 1
        val channelId = "download_channel"
        val channelName = "Download Channel"

        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)

        val notificationBuilder = NotificationCompat.Builder(requireContext(), channelId)
            .setContentTitle(resources.getString(R.string.downloading_docx))
            .setContentText(resources.getString(R.string.download_in_progress))
            .setSmallIcon(R.drawable.download).setPriority(NotificationCompat.PRIORITY_LOW)
            .setProgress(100, 0, true).setOngoing(true)

        notificationManager.notify(notificationId, notificationBuilder.build())

        withContext(Dispatchers.IO) {
            try {
                val document = XWPFDocument()

                for (progress in 1..100) {
                    // Update the notification progress
                    notificationBuilder.setProgress(100, progress, false)
                    notificationManager.notify(notificationId, notificationBuilder.build())
                    // Simulate some work being done
                    withContext(Dispatchers.IO) {
                        Thread.sleep(50)
                    }
                }

                val paragraph = document.createParagraph()
                val run = paragraph.createRun()
                run.setText(note.noteText)

                // Specify the directory and filename for saving the DOCX file
                val mDirectory = Environment.DIRECTORY_DOWNLOADS
                val mFilename = SimpleDateFormat(
                    "yyyyMMdd_HHmmss", Locale.getDefault()
                ).format(System.currentTimeMillis()) + "_" + note.title
                val mFilePath = Environment.getExternalStoragePublicDirectory(mDirectory)
                    .toString() + "/" + mFilename + ".docx"

                // Save the DOCX document to a file
                val outputStream = FileOutputStream(mFilePath)
                document.write(outputStream)
                outputStream.close()

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context, "$mFilename.docx is created", Toast.LENGTH_SHORT
                    ).show()
                }

                // Send broadcast when download is complete
                withContext(Dispatchers.Main) {
                    val downloadCompleteIntent = Intent("PDF_DOWNLOAD_COMPLETE")
                    downloadCompleteIntent.putExtra("file_path", mFilePath)
                    context?.sendBroadcast(downloadCompleteIntent)
                }
            } catch (e: Exception) {
                Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
            } finally {
                // Remove the ongoing notification when the download is complete
                notificationManager.cancel(notificationId)
            }
        }
    }

    // Import necessary classes
    private suspend fun saveExcel(note: NoteEntity) {
        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = 1
        val channelId = "download_channel"
        val channelName = "Download Channel"

        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)

        val notificationBuilder = NotificationCompat.Builder(requireContext(), channelId)
            .setContentTitle(resources.getString(R.string.download_excel))
            .setContentText(resources.getString(R.string.download_in_progress))
            .setSmallIcon(R.drawable.download).setPriority(NotificationCompat.PRIORITY_LOW)
            .setProgress(100, 0, true).setOngoing(true)

        notificationManager.notify(notificationId, notificationBuilder.build())
        withContext(Dispatchers.IO) {
            try {
                val workbook = HSSFWorkbook()
                val sheet = workbook.createSheet(note.title)

                for (progress in 1..100) {
                    // Update the notification progress
                    notificationBuilder.setProgress(100, progress, false)
                    notificationManager.notify(notificationId, notificationBuilder.build())
                    // Simulate some work being done
                    Thread.sleep(50)
                }
                val row = sheet.createRow(0)
                val cell = row.createCell(0)
                cell.setCellValue(note.noteText)

                // Specify the directory and filename for saving the Excel file
                val mDirectory = Environment.DIRECTORY_DOWNLOADS
                val mFilename = SimpleDateFormat(
                    "yyyyMMdd_HHmmss", Locale.getDefault()
                ).format(System.currentTimeMillis()) + "_" + note.title
                val mFilePath = Environment.getExternalStoragePublicDirectory(mDirectory)
                    .toString() + "/" + mFilename + ".xlsx"

                // Write the workbook to a file
                val fileOutputStream = FileOutputStream(mFilePath)
                workbook.write(fileOutputStream)
                fileOutputStream.close()

                // Display a toast message
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context, "$mFilename.xls is created", Toast.LENGTH_SHORT
                    ).show()
                }

                // Send broadcast when download is complete
                withContext(Dispatchers.Main) {
                    val downloadCompleteIntent = Intent("PDF_DOWNLOAD_COMPLETE")
                    downloadCompleteIntent.putExtra("file_path", mFilePath)
                    context?.sendBroadcast(downloadCompleteIntent)
                }

                Log.d("saveExcel1", "error 1")
            } catch (e: Exception) {
                // Handle exceptions
                Log.d("saveExcel", e.message.toString())
                Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
            } finally {
                // Remove the ongoing notification when the download is complete
                notificationManager.cancel(notificationId)
            }
        }
    }

    private fun disableSelection() {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            com.example.noteappandroid.utils.Constants.DisableSelection,
            true
        )
    }


    companion object {
        var noteId: Long? = null
    }

}