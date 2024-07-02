package com.example.noteappandroid.ui.home

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
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.noteappandroid.R
import com.example.noteappandroid.adapter.NoteAdapter
import com.example.noteappandroid.databinding.FragmentHomeBinding
import com.example.noteappandroid.entity.NoteEntity
import com.example.noteappandroid.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Home : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var notesAdapter: NoteAdapter

    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRv()
        collectNotes()

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(Constants.DisableSelection)
            ?.observe(viewLifecycleOwner) {
                if (it) {
                    notesAdapter.clearSelection()
                }
            }

        binding.imgCreateFolder.setOnClickListener {
            findNavController().navigate(R.id.action_home2_to_listFolders)
        }

        binding.fabCreateNoteBtn.setOnClickListener {
            findNavController().navigate(R.id.action_home2_to_createNote)
        }

        binding.searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                viewModel.onSearchQueryChanged(p0.toString())
                return true
            }
        })
    }

    private fun collectNotes() = viewLifecycleOwner.lifecycleScope.launch {
        viewModel.notes.collectLatest {
            if (it.isEmpty()) {
                binding.recyclerView.visibility = View.GONE
                binding.layoutNoData.visibility = View.VISIBLE
            } else {
                notesAdapter.submitList(it)
                binding.recyclerView.visibility = View.VISIBLE
                binding.layoutNoData.visibility = View.GONE
            }
        }
    }

    private val onClicked = object : NoteAdapter.OnItemClickListener {
        override fun onClicked(notesId: Long) {
            val action = HomeDirections.actionHome2ToCreateNote(notesId)
            findNavController().navigate(action)
        }

        override fun onLongClicked(note: NoteEntity) {
            val action = HomeDirections.actionHome2ToNoteDialogFragment(note.id)
            findNavController().navigate(action)
        }
    }

    private fun setUpRv() = binding.apply {
        notesAdapter = NoteAdapter().apply { setOnClickListener(onClicked) }
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.adapter = notesAdapter
    }
}