package com.example.noteappandroid.ui.create_notes

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.Html
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.AlignmentSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.util.Patterns
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.noteappandroid.R
import com.example.noteappandroid.adapter.FolderAdapter
import com.example.noteappandroid.adapter.LabelAdapter
import com.example.noteappandroid.databinding.FragmentCreateNoteBinding
import com.example.noteappandroid.entity.Folder
import com.example.noteappandroid.entity.Label
import com.example.noteappandroid.entity.NoteEntity
import com.example.noteappandroid.listeners.FolderClickListener
import com.example.noteappandroid.ui.folder.FolderViewModel
import com.example.noteappandroid.utils.FileHelperUtils
import com.example.noteappandroid.utils.Utils.EMPTY_STRING
import com.example.noteappandroid.utils.Utils.makeGone
import com.example.noteappandroid.utils.Utils.makeVisible
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.xwpf.usermodel.XWPFDocument
import pub.devrel.easypermissions.EasyPermissions
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class CreateNote : Fragment(), TextToSpeech.OnInitListener, FolderClickListener,
    EasyPermissions.PermissionCallbacks, LabelAdapter.ILabelClick {
    private lateinit var binding: FragmentCreateNoteBinding
    private val viewModel by viewModels<CreateNoteViewModel>()
    private val labelViewModel by viewModels<LabelViewModel>()

    private var noteIdFromHome: Long? = null
    private var currentFolderId: Long? = null
    private var isFromCurrentFolder: Boolean? = false
    var selectedColor = "#3e434e"
    private var currentTime: String? = null

    private var READ_STORAGE_CODE = 123
    private val REQUEST_CODE_SPEECH_INPUT = 150
    private val REQUEST_CODE_IMPORT_FILE = 152
    private val NOTIFICATION_CODE = 100

    private lateinit var textToSpeech: TextToSpeech

    private var webLink = EMPTY_STRING
    private var selectedImagePath = EMPTY_STRING

    private val folderViewModel by viewModels<FolderViewModel>()
    private lateinit var adapter: FolderAdapter
    private var folderId: Long = 1
    private var layoutNone: MaterialTextView? = null

    private var isBold = false
    private var isItalic = false
    private var isUnderlined = false


    private val mFileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            run {
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    val data: Intent? = result.data
                    val uri: Uri? = data?.data
                    uri?.let {
                        val filePath =
                            context?.let { it1 ->
                                FileHelperUtils.getPath(
                                    it1, it
                                )
                            }
                        if (filePath != null) {
                            if (filePath.endsWith(".docx")) {
                                importWordFile(filePath)
                            }
                        } else {
                            Toast.makeText(
                                context, "Please select a valid Excel file", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

    private fun launchFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type =
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document" // MIME type for Word files
        Log.d("mFileLauncher", mFileLauncher.toString())
        mFileLauncher.launch(intent)
    }


    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Không cần xử lý trước khi văn bản thay đổi
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            binding.txtCharacters.text =
                resources.getString(R.string.characters, s?.length.toString())

            if (isBold) {
                val spannable = SpannableStringBuilder(s)
                spannable.setSpan(
                    StyleSpan(Typeface.BOLD),
                    start,
                    start + count,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                binding.etNoteDesc.removeTextChangedListener(this)
                binding.etNoteDesc.text = spannable
                binding.etNoteDesc.setSelection(start + count)
                binding.etNoteDesc.addTextChangedListener(this)
            }

            if (isItalic) {
                val spannable = SpannableStringBuilder(s)
                spannable.setSpan(
                    StyleSpan(Typeface.ITALIC),
                    start,
                    start + count,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                binding.etNoteDesc.removeTextChangedListener(this)
                binding.etNoteDesc.text = spannable
                binding.etNoteDesc.setSelection(start + count)
                binding.etNoteDesc.addTextChangedListener(this)
            }
        }

        override fun afterTextChanged(s: Editable?) {
            // Không cần xử lý sau khi văn bản thay đổi
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        textToSpeech = TextToSpeech(context, this)

        arguments?.let {
            noteIdFromHome = it.getLong("noteIdFromHome", -1)
            currentFolderId = it.getLong("currentFolderId", -1)
            isFromCurrentFolder = it.getBoolean("isFromCurrentFolder", false)
        }
        noteIdFromHome?.let {
            viewModel.setNoteId(it)
            Log.d("noteIdFromHome", it.toString())
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            EasyPermissions.requestPermissions(
                this, getString(R.string.storage_permission_text),
                READ_STORAGE_CODE, Manifest.permission.READ_MEDIA_IMAGES
            )
        } else {
            EasyPermissions.requestPermissions(
                this, getString(R.string.storage_permission_text),
                READ_STORAGE_CODE, Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noteIdFromHome?.let { labelViewModel.getLabelsForNoteId(it) }
        val labelAdapter = LabelAdapter(this)
        binding.rvLabels.adapter = labelAdapter
        binding.rvLabels.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        viewLifecycleOwner.lifecycleScope.launch {
            labelViewModel.labelsForNoteId.collectLatest {
                Log.d("TAG", "initViews: ${Gson().toJson(it)}")
                labelAdapter.updateLabels(it)
            }
        }
        initViews()
        collectNotes()
    }

    private fun collectNotes() = viewLifecycleOwner.lifecycleScope.launch {
        viewModel.note.collectLatest {
            it?.let(this@CreateNote::setNoteDataInUI)
        }
    }

    private fun setNoteDataInUI(note: NoteEntity) = binding.apply {
        colorView.setBackgroundColor(Color.parseColor(note.color))
        etNoteTitle.setText(note.title)
        val html = note.noteText
        note.noteText?.let { Log.d("HTMLLLLL", cleanHtml(it)) }
        val trimmedHtml = html?.let { cleanHtml(it) }
        val spannable = Html.fromHtml(
            trimmedHtml, Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH
        )
        binding.etNoteDesc.setText(spannable)
        if (note.folderId != null) {
            folderId = note.folderId ?: 1
        }
        txtCharacters.text = resources.getString(
            R.string.characters, note.noteText?.length.toString()
        )
        if (note.imgPath != EMPTY_STRING) {
            selectedImagePath = note.imgPath.orEmpty()
            imgNote.setImageBitmap(BitmapFactory.decodeFile(note.imgPath))
            makeVisible(layoutImage, binding.imgNote, binding.imgDelete)
        } else {
            makeGone(
                layoutImage, binding.imgNote, binding.imgDelete
            )
        }
        if (note.storeWebLink != EMPTY_STRING) {
            webLink = note.storeWebLink.orEmpty()
            tvWebLink.text = note.storeWebLink
            makeVisible(
                layoutWebUrl, imgUrlDelete
            )
            etWebLink.setText(note.storeWebLink)
        } else {
            makeGone(imgUrlDelete, layoutWebUrl)
        }
    }

    private fun cleanHtml(html: String): String {
        return html.replace(
            Regex("(?m)^[ \t]*\r?\n"), ""
        )
            // Xóa các dòng chỉ chứa dấu cách hoặc dấu tab
            .replace("<br><br>", "<br>")
            //Thay thế các thẻ <br><br> thành <br>
            .replace(Regex("</p>\\s*<p>"), "</p><p>")
            // Xóa khoảng trắng giữa các thẻ </p> và <p>
            .replace(Regex("<p[^>]*>"), "") // Xóa thẻ <p> mở
            .replace("</p>", "<br>") // Thay thế thẻ </p> bằng <br>
    }


    private fun initViews() = binding.apply {
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            broadcastReceiver, IntentFilter("bottom_sheet_action")
        )

        colorView.setBackgroundColor(Color.parseColor(selectedColor))

        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault())
        currentTime = sdf.format(Date())

        txtDateTime.text = currentTime
        etNoteDesc.addTextChangedListener(textWatcher)

        binding.iconAddNewLabel.setOnClickListener {
            val action = CreateNoteDirections.actionCreateNoteToLabelDialogFragment(
                folderId,
                noteIdFromHome ?: -1,
                true
            )
            findNavController().navigate(action)
        }


        if (noteIdFromHome != -1L) {
            imgExportNote.visibility = View.VISIBLE
            imgExportNote.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (EasyPermissions.hasPermissions(
                            requireContext(),
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                    ) {
                        showSaveFormatDialog()
                    } else {
                        EasyPermissions.requestPermissions(
                            requireActivity(),
                            getString(R.string.storage_permission_text),
                            NOTIFICATION_CODE,
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                    }
                } else {
                    showSaveFormatDialog()
                }
            }

            imgShareNote.visibility = View.VISIBLE
            imgShareNote.setOnClickListener {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                val html = viewModel.note.value?.noteText
                val trimmedHtml = html?.let { cleanHtml(it) }
                val spannable = Html.fromHtml(
                    trimmedHtml, Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH
                )
                shareIntent.putExtra(Intent.EXTRA_TEXT, spannable.toString())
                val chooser = Intent.createChooser(shareIntent, getString(R.string.share_note_via))
                startActivity(chooser)
            }
        } else {
            binding.imgExportNote.visibility = View.GONE
        }

        imgDone.setOnClickListener {
            viewModel.note.value?.let { updateNote(it) } ?: saveNote()
        }

        imgImportFile.setOnClickListener {
            showImportAlertDialog(requireContext())
        }

        imgAddToFolder.setOnClickListener {
            showDetailDialog()
        }

        imgBack.setOnClickListener {
            findNavController().popBackStack()
        }

        imgMore.setOnClickListener {
            val action = CreateNoteDirections.actionCreateNoteToNoteBottomSheetFragment(
                viewModel.noteId.value ?: -1
            )
            findNavController().navigate(action)
        }

        imgDelete.setOnClickListener {
            selectedImagePath = EMPTY_STRING
            layoutImage.visibility = View.GONE
        }

        imgReminder.setOnClickListener {
            val action = viewModel.note.value?.id?.let { it1 ->
                CreateNoteDirections.actionCreateNoteToNoteReminderDialog(
                    1,   //args.folderId
                    it1
                )
            }
//            Log.d("action", action.toString())
            if (action != null) {
                findNavController().navigate(action)
            }
        }

        imgMic.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(), Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startSpeechRecognition()
            } else {
                requestSpeechRecognitionPermission()
            }
        }

        imgHeader.setOnClickListener {
            addBulletPoint(binding.imgHeader)
        }

        imgChooseText.setOnClickListener {
            binding.layoutEditText.visibility = View.VISIBLE
            binding.flexboxLayout.visibility = View.GONE

            binding.iconBold.setOnClickListener {
                isBold = !isBold
                it.isSelected = isBold
                applyStyle(StyleSpan(Typeface.BOLD))
            }

            binding.iconItalic.setOnClickListener {
                isItalic = !isItalic
                it.isSelected = isItalic
                applyStyle(StyleSpan(Typeface.ITALIC))
            }

            binding.iconUnderline.setOnClickListener {
                isUnderlined = !isUnderlined
                it.isSelected = isUnderlined
//                applyStyle(StyleSpan(Typeface.BOLD_ITALIC))
                underlineText()
            }

            binding.iconAlignLeft.setOnClickListener {
                alignText(Layout.Alignment.ALIGN_NORMAL)
            }

            binding.iconAlignCenter.setOnClickListener {
                alignText(Layout.Alignment.ALIGN_CENTER)
            }

            binding.iconAlignRight.setOnClickListener {
                alignText(Layout.Alignment.ALIGN_OPPOSITE)
            }

            binding.iconDecreaseSize.setOnClickListener {
                // Get the current text size in pixels
                val currentTextSizePx = binding.etNoteDesc.textSize
                // Convert from pixels to scaled value (sp)
                val currentTextSizeSp =
                    currentTextSizePx / binding.etNoteDesc.resources.displayMetrics.scaledDensity
                // Decrease text size in sp
                val newTextSizeSp = currentTextSizeSp - 2
                // Apply the new text size in sp
                binding.etNoteDesc.setTextSize(TypedValue.COMPLEX_UNIT_SP, newTextSizeSp)
                if (currentTextSizeSp < 8) {
                    binding.etNoteDesc.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8f)
                }
            }

            binding.iconIncreaseSize.setOnClickListener {
                val currentTextSizePx = binding.etNoteDesc.textSize
                val currentTextSizeSp =
                    currentTextSizePx / binding.etNoteDesc.resources.displayMetrics.scaledDensity
                val newTextSizeSp = currentTextSizeSp + 2
                binding.etNoteDesc.setTextSize(TypedValue.COMPLEX_UNIT_SP, newTextSizeSp)
            }

            binding.iconCloseEdit.setOnClickListener {
                binding.layoutEditText.visibility = View.GONE
                binding.flexboxLayout.visibility = View.VISIBLE
            }
        }



        btnOk.setOnClickListener {
            if (etWebLink.text.toString().trim().isNotEmpty()) {
                checkWebUrl()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.url_require),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        btnCancel.setOnClickListener {
            if (viewModel.noteId.value != null) {
                tvWebLink.makeVisible()
                layoutWebUrl.makeGone()
            } else {
                layoutWebUrl.makeGone()
            }
        }

        imgUrlDelete.setOnClickListener {
            webLink = EMPTY_STRING
            makeGone(tvWebLink, imgUrlDelete, layoutWebUrl)
        }

        tvWebLink.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(etWebLink.text.toString()))
            startActivity(intent)
        }
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (p1 == null || view == null)
                return
            val actionColor = p1.getStringExtra(getString(R.string.action))

            view?.let { fragmentView ->
                val binding = FragmentCreateNoteBinding.bind(fragmentView)

                when (actionColor) {
                    getString(R.string.blue) -> {
                        selectedColor = p1.getStringExtra(SELECTED_COLOR) ?: ""
                        binding.colorView.setBackgroundColor(Color.parseColor(selectedColor))
                    }

                    getString(R.string.cyan) -> {
                        selectedColor = p1.getStringExtra(SELECTED_COLOR) ?: ""
                        binding.colorView.setBackgroundColor(Color.parseColor(selectedColor))
                    }

                    getString(R.string.green) -> {
                        selectedColor = p1.getStringExtra(SELECTED_COLOR) ?: ""
                        binding.colorView.setBackgroundColor(Color.parseColor(selectedColor))
                    }

                    getString(R.string.orange) -> {
                        selectedColor = p1.getStringExtra(SELECTED_COLOR) ?: ""
                        binding.colorView.setBackgroundColor(Color.parseColor(selectedColor))
                    }

                    getString(R.string.purple) -> {
                        selectedColor = p1.getStringExtra(SELECTED_COLOR) ?: ""
                        binding.colorView.setBackgroundColor(Color.parseColor(selectedColor))
                    }

                    getString(R.string.red) -> {
                        selectedColor = p1.getStringExtra(SELECTED_COLOR) ?: ""
                        binding.colorView.setBackgroundColor(Color.parseColor(selectedColor))
                    }

                    getString(R.string.yellow) -> {
                        selectedColor = p1.getStringExtra(SELECTED_COLOR) ?: ""
                        binding.colorView.setBackgroundColor(Color.parseColor(selectedColor))
                    }

                    getString(R.string.brown) -> {
                        selectedColor = p1.getStringExtra(SELECTED_COLOR) ?: ""
                        binding.colorView.setBackgroundColor(Color.parseColor(selectedColor))
                    }

                    getString(R.string.indigo) -> {
                        selectedColor = p1.getStringExtra(SELECTED_COLOR) ?: ""
                        binding.colorView.setBackgroundColor(Color.parseColor(selectedColor))
                    }

                    getString(R.string.image) -> {
                        readStorageTask()
                        binding.layoutWebUrl.makeGone()
                    }

                    getString(R.string.webUrl) -> {
                        binding.layoutWebUrl.visibility = View.VISIBLE
                    }

                    getString(R.string.deleteNote) -> {
                        context?.let {
                            MaterialAlertDialogBuilder(it)
                                .setTitle(resources.getString(R.string.title))
                                .setMessage(resources.getString(R.string.are_you_sure_to_delete_the_note))
                                .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->

                                }
                                .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                                    dialog.dismiss()
                                }
                                .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                                    deleteNote()
                                    Toast.makeText(
                                        context,
                                        resources.getString(R.string.detele_note_success),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .show()
                        }
                    }

                    else -> {
                        binding.layoutImage.visibility = View.GONE
                        binding.imgNote.visibility = View.GONE
                        binding.layoutWebUrl.visibility = View.GONE
                        selectedColor = p1.getStringExtra(SELECTED_COLOR) ?: ""
                        makeGone(with(binding) {
                            layoutImage
                            imgNote
                            layoutWebUrl
                        })
                        selectedColor = p1.getStringExtra(SELECTED_COLOR).orEmpty()
                        binding.colorView.setBackgroundColor(Color.parseColor(selectedColor))
                    }
                }
            }
        }
    }

    private fun updateNote(note: NoteEntity) = viewLifecycleOwner.lifecycleScope.launch {
        val spannable = binding.etNoteDesc.text
        // Chuyển đổi Spannable sang HTML
        var html = Html.toHtml(spannable, Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL).trim()
        // Loại bỏ các dòng trắng không mong muốn
        html = html.replace(Regex("(?m)^[ \t]*\r?\n"), "")
        // Xóa các dòng chỉ chứa dấu cách hoặc dấu tab
        html = html.replace("<br><br>", "<br>")
        // Thay thế các thẻ <br><br> thành <br>
        html = html.replace(Regex("</p>\\s*<p>"), "</p><p>")
        // Xóa khoảng trắng giữa các thẻ </p> và <p>
        note.apply {
            title = binding.etNoteTitle.text.toString()
            noteText = html
            dateTime = currentTime
            color = selectedColor
            imgPath = selectedImagePath
            storeWebLink = webLink
        }.also { viewModel.updateNote(it) }
        binding.etNoteTitle.setText(EMPTY_STRING)
        binding.etNoteDesc.setText(EMPTY_STRING)
        makeGone(with(binding) {
            layoutImage
            imgNote
            tvWebLink
        })
        requireActivity().supportFragmentManager.popBackStack()
    }

    private fun deleteNote() = viewLifecycleOwner.lifecycleScope.launch {
        viewModel.deleteNote()
        requireActivity().supportFragmentManager.popBackStack()
    }

    private fun checkWebUrl() {
        if (Patterns.WEB_URL.matcher(binding.etWebLink.text.toString()).matches()) {
            binding.layoutWebUrl.makeGone()
            binding.etWebLink.isEnabled = false
            webLink = binding.etWebLink.text.toString()
            binding.tvWebLink.makeVisible()
            binding.tvWebLink.text = binding.etWebLink.text.toString()
        } else {
            Toast.makeText(requireContext(), getString(R.string.url_validation), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun saveNote() {

        val etNoteTitle = view?.findViewById<EditText>(R.id.etNoteTitle)
        val etNoteDesc = view?.findViewById<EditText>(R.id.etNoteDesc)

        when {
            etNoteTitle?.text.isNullOrEmpty() -> {
                Snackbar.make(
                    requireView(),
                    getString(R.string.title_require),
                    Snackbar.LENGTH_LONG
                )
                    .setAction(getString(R.string.snackbarok)) {

                    }.show()
            }

            etNoteDesc?.text.isNullOrEmpty() -> {
                Snackbar.make(
                    requireView(),
                    getString(R.string.empty_note_description_warning),
                    Snackbar.LENGTH_LONG
                ).setAction(getString(R.string.snackbarok)) {

                }.show()
            }

            else -> {
                val html = binding.etNoteDesc.text.toString()
                val trimmedHtml = html.trim()
                val spannable = Html.escapeHtml(trimmedHtml)
                viewLifecycleOwner.lifecycleScope.launch {
                    NoteEntity().apply {
                        title = etNoteTitle?.text.toString()
                        folderId =
                            if (currentFolderId != 1L && isFromCurrentFolder == true) currentFolderId else 1
                        noteText = spannable.toString()
                        dateTime = currentTime
                        color = selectedColor
                        imgPath = selectedImagePath
                        storeWebLink = webLink
                    }.also {
                        viewModel.saveNote(it)
                    }
                    etNoteTitle?.setText(EMPTY_STRING)
                    etNoteDesc?.setText(EMPTY_STRING)
                    makeGone(with(binding) {
                        layoutImage
                        imgNote
                        tvWebLink
                    })
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }
    }

    private fun showImportAlertDialog(context: Context) {
        MaterialAlertDialogBuilder(context).setTitle(resources.getString(R.string.alert_import_title))
            .setMessage(resources.getString(R.string.alert_import_desc))

            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                dialog.dismiss()
            }.setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    val permission = Manifest.permission.READ_EXTERNAL_STORAGE
                    if (ContextCompat.checkSelfPermission(
                            requireActivity(), permission
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        launchFilePicker()
                    } else {
                        ActivityCompat.requestPermissions(
                            requireActivity(), arrayOf(permission), REQUEST_CODE_IMPORT_FILE
                        )
                    }
                } else {
                    val permissions = arrayOf(
                        Manifest.permission.READ_MEDIA_AUDIO,
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.READ_MEDIA_IMAGES
                    )

                    var allPermissionsGranted = true

                    for (permission in permissions) {
                        if (ContextCompat.checkSelfPermission(
                                requireActivity(), permission
                            ) == PackageManager.PERMISSION_DENIED
                        ) {
                            allPermissionsGranted = false
                            break
                        }
                    }

                    if (!allPermissionsGranted) {
                        ActivityCompat.requestPermissions(
                            requireActivity(), permissions, REQUEST_CODE_IMPORT_FILE
                        )
                    } else {
                        launchFilePicker()
                    }
                }
            }.show()
    }

    private fun importWordFile(filePath: String): String {
        val document = XWPFDocument(FileInputStream(filePath))
        val paragraphs = document.paragraphs
        val result = StringBuilder()

        for (paragraph in paragraphs) {
            val text = paragraph.text
            result.append(text).append("\n")
        }

        binding.etNoteDesc.text = Editable.Factory.getInstance().newEditable(result)
        return result.toString()
    }

    override fun onInit(p0: Int) {
        if (p0 == TextToSpeech.SUCCESS) {
            val result = textToSpeech.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                val installIntent = Intent()
                installIntent.action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
                startActivity(installIntent)
            }
        } else {
            Log.e("TTSpeech2", "Initialization failed with status: $p0")
            Toast.makeText(context, "Initialization failed.", Toast.LENGTH_SHORT).show()
        }
    }

    private val mSpeechLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            run {
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    val data: Intent? = result.data
                    val speechResults =
                        data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

                    if (!speechResults.isNullOrEmpty()) {
                        binding.etNoteDesc.text = Editable.Factory.getInstance().newEditable(
                            speechResults.get(0).toString()
                        )
                    } else {
                        Toast.makeText(context, "No speech results found", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

    private fun startSpeechRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")

        try {
            mSpeechLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestSpeechRecognitionPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_CODE_SPEECH_INPUT
        )
    }

    private fun addBulletPoint(view: View) {
        val selectionStart = binding.etNoteDesc.selectionStart
        val selectionEnd = binding.etNoteDesc.selectionEnd

        val bulletPoint = "\u25A1 " // Dấu đầu dòng, có thể thay đổi theo nhu cầu

        // Lấy vị trí bắt đầu và kết thúc của dòng hiện tại
        val startOfLine = binding.etNoteDesc.layout.getLineStart(
            binding.etNoteDesc.layout.getLineForOffset(selectionStart)
        )
        // Lấy text hiện tại trong EditText
        val text = binding.etNoteDesc.text
        // Chèn dấu đầu dòng vào đầu dòng hiện tại
        text?.insert(startOfLine, bulletPoint)

        // Đặt lại con trỏ sau dấu đầu dòng
        val newSelectionStart = selectionStart + bulletPoint.length
        val newSelectionEnd = selectionEnd + bulletPoint.length
        binding.etNoteDesc.setSelection(newSelectionStart, newSelectionEnd)
    }

    private fun applyStyle(style: Any) {
        val editableText = binding.etNoteDesc.text
        val start = binding.etNoteDesc.selectionStart
        val end = binding.etNoteDesc.selectionEnd

        if (start != end) {
            val spannable = SpannableStringBuilder(editableText)
            spannable.setSpan(style, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.etNoteDesc.text = spannable
        }
    }

    private fun underlineText() {
        val editText = binding.etNoteDesc
        val start = editText.selectionStart
        val end = editText.selectionEnd

        val spannableString = SpannableString(editText.text)

        if (start != end) {
            spannableString.setSpan(
                UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else {
            spannableString.setSpan(
                UnderlineSpan(), 0, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        editText.setText(spannableString)
        editText.setSelection(start, end)
    }


    private fun alignText(alignment: Layout.Alignment) {
        val start = binding.etNoteDesc.selectionStart
        val end = binding.etNoteDesc.selectionEnd

        if (start == end) {
            // No text selected, apply alignment to the entire text
            val spannable = SpannableStringBuilder(binding.etNoteDesc.text)
            val alignmentSpan = AlignmentSpan.Standard(alignment)
            spannable.setSpan(
                alignmentSpan,
                0,
                spannable.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            binding.etNoteDesc.setText(spannable)
        } else {
            // Text is selected, apply alignment only to the selected text
            val spannable = binding.etNoteDesc.text as Spannable
            val alignmentSpan = AlignmentSpan.Standard(alignment)
            spannable.setSpan(alignmentSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
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
        folderViewModel.getAllFolders()
        layoutNone = dialogChooseParent.findViewById(R.id.tv_parent_folder_option)
//        window.setBackgroundDrawableResource(R.drawable.rounded_corner)
        layoutNone?.setOnClickListener {
            adapter.setRecyclerViewPosition(RecyclerView.NO_POSITION)
            layoutNone?.setBackgroundColor(Color.LTGRAY)
            folderId = 1
        }

        val rcv = dialogChooseParent.findViewById<RecyclerView>(R.id.rvListFolders)
        adapter = FolderAdapter(this)
        rcv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
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
                viewModel.note.value.let {
                    Log.d("TAG", "showDetailDialog1: ${Gson().toJson(it)}")
                    it?.folderId = folderId
                    if (it != null) {
                        Log.d("TAG", "showDetailDialog2: ${Gson().toJson(it)}")
                        viewModel.updateNote(it)
                    }
                }
            }
            dialogChooseParent.dismiss()
        }
        dialogChooseParent.show()
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
            lifecycleScope.launch { saveExcel(viewModel.note.value!!) }
            dialog.dismiss()
        }

        btnDownloadWord.setOnClickListener {
            lifecycleScope.launch { saveDocx(viewModel.note.value!!) }
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
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
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

                val html = note.noteText
                val trimmedHtml = html?.let { cleanHtml(it) }
                val spannable = Html.fromHtml(
                    trimmedHtml, Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH
                )
                run.setText(spannable.toString())

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

                // Send broadcast when download is complete
                withContext(Dispatchers.Main) {
                    val downloadCompleteIntent = Intent("PDF_DOWNLOAD_COMPLETE")
                    Log.d("TAG", "saveDocx: $mFilePath")
                    downloadCompleteIntent.setPackage(requireContext().packageName)
                    downloadCompleteIntent.putExtra("file_path", mFilePath)
                    Log.d("Broadcast", "downloadCompleteIntent : ${requireContext()}")
                    requireContext().sendBroadcast(downloadCompleteIntent)
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context, "$mFilename.docx is created", Toast.LENGTH_SHORT
                    ).show()
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
            .setSmallIcon(R.drawable.download).setPriority(NotificationCompat.PRIORITY_HIGH)
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

                val html = note.noteText
                val trimmedHtml = html?.let { cleanHtml(it) }
                val spannable = Html.fromHtml(
                    trimmedHtml, Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH
                )
                cell.setCellValue(spannable.toString())

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
                    downloadCompleteIntent.setPackage(requireContext().packageName)
                    downloadCompleteIntent.putExtra("file_path", mFilePath)
                    requireContext().sendBroadcast(downloadCompleteIntent)
                }

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

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        if (requestCode == NOTIFICATION_CODE) {
            showSaveFormatDialog()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (requestCode == NOTIFICATION_CODE) {
            Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun handleFolderClick(folder: Folder) {
        layoutNone?.setBackgroundColor(Color.WHITE)
    }

    private fun hasReadStoragePerm(): Boolean {
        return EasyPermissions.hasPermissions(
            requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun hasReadImagePerm(): Boolean {
        return EasyPermissions.hasPermissions(
            requireContext(), Manifest.permission.READ_MEDIA_IMAGES
        )
    }

    private fun readStorageTask() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (hasReadImagePerm()) {
                pickImageFromGallery()
            } else {
                EasyPermissions.requestPermissions(
                    this, getString(R.string.storage_permission_text),
                    READ_STORAGE_CODE, Manifest.permission.READ_MEDIA_IMAGES
                )
            }
        } else {
            if (hasReadStoragePerm()) {
                pickImageFromGallery()
            } else {
                EasyPermissions.requestPermissions(
                    this, getString(R.string.storage_permission_text),
                    READ_STORAGE_CODE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getResultForImage.launch(intent)
        } else {
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                getResultForImage.launch(intent)
            }
        }
    }

    private fun getPathFromUri(contentUri: Uri): String? {
        val filePath: String?
        val cursor = requireActivity().contentResolver.query(contentUri, null, null, null, null)
        if (cursor == null) {
            filePath = contentUri.path
        } else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath
    }

    // Image Integration
    private val getResultForImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                if (it.data != null) {
                    val selectedImageUrl = it.data!!.data
                    if (selectedImageUrl != null) {
                        try {
                            val inputStream =
                                requireActivity().contentResolver.openInputStream(selectedImageUrl)
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            binding.imgNote.setImageBitmap(bitmap)
                            binding.imgNote.makeVisible()
                            binding.layoutImage.makeVisible()
                            selectedImagePath = getPathFromUri(selectedImageUrl).orEmpty()
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

    override fun handleLabelClick(label: Label) {
        Log.d("TAG", "HHHH")
    }

    override fun handleLabelLongClick(label: Label) {
        MaterialAlertDialogBuilder(requireContext()).setTitle(resources.getString(R.string.title))
            .setMessage(resources.getString(R.string.are_you_sure_to_delete_this_label))
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which -> dialog.dismiss() }
            .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                labelViewModel.deleteLabel(label)
            }.show()
    }


    companion object {
        const val SELECTED_COLOR = "selectedColor"
    }

}