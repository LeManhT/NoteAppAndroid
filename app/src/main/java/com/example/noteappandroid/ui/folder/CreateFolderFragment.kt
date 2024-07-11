package com.example.noteappandroid.ui.folder

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.noteappandroid.R
import com.example.noteappandroid.adapter.FolderAdapter
import com.example.noteappandroid.databinding.FragmentCreateFolderBinding
import com.example.noteappandroid.entity.Folder
import com.example.noteappandroid.listeners.FolderClickListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateFolderFragment : Fragment(), FolderClickListener {
    private lateinit var binding: FragmentCreateFolderBinding
    private val colorController = ColorController()
    private val folderViewModel by viewModels<FolderViewModel>()
    private lateinit var adapter: FolderAdapter
    private var parentFolderId: Long = -1
    private var layoutNone: MaterialTextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateFolderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        folderViewModel.getAllFolders()

        binding.tb.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

//        binding.rv.adapter = colorController.adapter
//        val colors = listOf(
//            R.color.colorPrimary,
//            R.color.colorBackground,
//            R.color.colorSurface,
//            R.color.colorBackground
//        )
//
//        colorController.colors = colors
//        colorController.requestModelBuild()

        binding.txtChooseParentFolder.setOnClickListener {
            showDetailDialog()
        }

        binding.btnCreate.setOnClickListener {
            val title = binding.til.editText?.text.toString()
            val folder = Folder(0, parentFolderId, title)
            folderViewModel.createFolder(folder)
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.create_folder_success),
                Toast.LENGTH_SHORT
            ).show()
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
        layoutNone = dialogChooseParent.findViewById(R.id.tv_parent_folder_option)
//        window.setBackgroundDrawableResource(R.drawable.rounded_corner)
        layoutNone?.setOnClickListener {
            adapter.setRecyclerViewPosition(RecyclerView.NO_POSITION)
            layoutNone?.setBackgroundColor(Color.LTGRAY)
            parentFolderId = -1
        }
        folderViewModel.getAllFolders()
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
                binding.txtChooseParentFolder.text = selectedFolder.title
                parentFolderId = selectedFolder.id
            } else {
                parentFolderId = -1
                binding.txtChooseParentFolder.text = resources.getString(R.string.none)
            }
            Log.d("TAG", "showDetailDialog: $parentFolderId")
            dialogChooseParent.dismiss()
        }
        dialogChooseParent.show()
    }

    override fun handleFolderClick(folder: Folder) {
        layoutNone?.setBackgroundColor(Color.WHITE)
    }
}