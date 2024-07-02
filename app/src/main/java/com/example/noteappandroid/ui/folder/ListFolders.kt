package com.example.noteappandroid.ui.folder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.noteappandroid.R
import com.example.noteappandroid.adapter.FolderAdapter
import com.example.noteappandroid.databinding.FragmentListFoldersBinding
import com.example.noteappandroid.entity.Folder
import com.example.noteappandroid.listeners.FolderClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListFolders : Fragment(), FolderClickListener {
    private lateinit var binding: FragmentListFoldersBinding
    private val folderViewModel: FolderViewModel by viewModels<FolderViewModel>()
    private lateinit var adapter: FolderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListFoldersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        folderViewModel.getRootFolders()

        adapter = FolderAdapter(this)
        binding.rvFolders.adapter = adapter
        binding.rvFolders.setHasFixedSize(true)
        binding.rvFolders.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        viewLifecycleOwner.lifecycleScope.launch {
            folderViewModel.folders.collect {
                if (it.isEmpty()) {
                    binding.rvFolders.visibility = View.GONE
                    binding.layoutNoData.visibility = View.VISIBLE
                } else {
                    adapter.updateData(it)
                    binding.rvFolders.visibility = View.VISIBLE
                    binding.layoutNoData.visibility = View.GONE
                }
            }
        }

        binding.cardCreateFolder.setOnClickListener {
            findNavController().navigate(R.id.action_listFolders_to_createFolderFragment)
        }
    }

    override fun handleFolderClick(folder: Folder) {
        val action =
            ListFoldersDirections.actionListFoldersToListNoteInFolder(
                folder.id,
            )
        findNavController().navigate(action)
    }
}