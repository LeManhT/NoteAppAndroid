package com.example.noteappandroid.ui.folder

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.noteappandroid.R
import com.example.noteappandroid.adapter.FolderAdapter
import com.example.noteappandroid.adapter.LabelAdapter
import com.example.noteappandroid.adapter.NoteAdapter
import com.example.noteappandroid.databinding.FragmentListNoteInFolderBinding
import com.example.noteappandroid.entity.Folder
import com.example.noteappandroid.entity.Label
import com.example.noteappandroid.entity.NoteEntity
import com.example.noteappandroid.listeners.FolderClickListener
import com.example.noteappandroid.ui.create_notes.CreateNoteViewModel
import com.example.noteappandroid.ui.create_notes.LabelViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListNoteInFolder : Fragment(), LabelAdapter.ILabelClick, FolderClickListener {
    private lateinit var binding: FragmentListNoteInFolderBinding
    private lateinit var adapterNotes: NoteAdapter
    private lateinit var labelAdapter: LabelAdapter
    private lateinit var folderAdapter: FolderAdapter
    private val noteViewModel by viewModels<CreateNoteViewModel>()
    private val folderViewModel by viewModels<FolderViewModel>()
    private val navArgs: ListNoteInFolderArgs by navArgs()
    private val labelViewModel by viewModels<LabelViewModel>()
    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        noteViewModel.setFolderId(navArgs.folderId)
        labelViewModel.getLabelsByFolderId(navArgs.folderId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListNoteInFolderBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(
            view,
            savedInstanceState
        )
        adapterNotes = NoteAdapter().apply { setOnClickListener(onClicked) }
        binding.rvNotesInFolder.adapter = adapterNotes
        binding.rvNotesInFolder.layoutManager =
            StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL
            )
        collectNotes()
        folderViewModel.getSubFolders(navArgs.folderId)
        binding.searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                noteViewModel.onSearchQueryChanged(p0.toString())
                return true
            }
        })
        binding.fabCreateNoteBtn.setOnClickListener {
            val action =
                ListNoteInFolderDirections.actionListNoteInFolderToCreateNote(
                    -1,
                    navArgs.folderId,
                    navArgs.folderId != 1L
                )
            findNavController().navigate(action)
        }
        labelAdapter = LabelAdapter(this)
        binding.rvLabelsInFolder.adapter = labelAdapter
        binding.rvLabelsInFolder.layoutManager =
            LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        lifecycleScope.launch {
            labelViewModel.labelsForFolderId.collect { labels ->
                labelAdapter.updateLabels(
                    labels
                )
            }
        }
        folderAdapter = FolderAdapter(this)
        binding.rvListSubFolders.adapter = folderAdapter
        binding.rvListSubFolders.layoutManager =
            LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
        viewLifecycleOwner.lifecycleScope.launch {
            folderViewModel.listSubFolders.collect { folders ->
                Log.d(
                    "TAG",
                    "listSubFolders: ${Gson().toJson(folders)}"
                )
                folderAdapter.updateData(folders)
            }
        }
//        binding.iconAddNewLabel.setOnClickListener { //
//        val action =
//                ListNoteInFolderDirections.actionListNoteInFolderToLabelDialogFragment(
//                    navArgs.folderId,
//                    noteViewModel.noteId.value ?: -1,
//                    false //                )
//            findNavController().navigate(action) //        }
    }

    private val onClicked = object : NoteAdapter.OnItemClickListener {
        override fun onClicked(noteId: Long, folderId: Long) {
            Log.d("HIHI", navArgs.folderId.toString())
            val action = ListNoteInFolderDirections.actionListNoteInFolderToCreateNote(
                noteId,
                navArgs.folderId,
                navArgs.folderId != 1L
            )
            findNavController().navigate(action)
        }

        override fun onLongClicked(note: NoteEntity) {
            val action =
                ListNoteInFolderDirections.actionListNoteInFolderToNoteDialogFragment(note.id)
            findNavController().navigate(
                action
            )
        }
    }

    override fun handleLabelClick(label: Label) {
        noteViewModel.onLabelSelected(label.id)
    }

    private fun collectNotes() = viewLifecycleOwner.lifecycleScope.launch {
        noteViewModel.notesInFolderWithSearchAndLabel.collectLatest {
            if (it.isEmpty()) {
                binding.rvNotesInFolder.visibility =
                    View.GONE
                binding.layoutNoData.visibility = View.VISIBLE
            } else {
                adapterNotes.submitList(it)
                binding.rvNotesInFolder.visibility =
                    View.VISIBLE
                binding.layoutNoData.visibility =
                    View.GONE
            }
        }
    }

    override fun handleFolderClick(folder: Folder) {
        val action =
            ListNoteInFolderDirections.actionListNoteInFolderSelf(folder.id)
        findNavController().navigate(
            action
        )
    }

    override fun handleLabelLongClick(label: Label) {
        MaterialAlertDialogBuilder(requireContext()).setTitle(resources.getString(R.string.title))
            .setMessage(resources.getString(R.string.are_you_sure_to_delete_this_label))
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which -> dialog.dismiss() }
            .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                labelViewModel.deleteLabel(label)
            }.show()
    }
}
