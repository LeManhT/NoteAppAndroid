package com.example.noteappandroid.ui.create_notes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.noteappandroid.base.BaseDialogFragment
import com.example.noteappandroid.databinding.FragmentLabelDialogBinding
import com.example.noteappandroid.entity.Label
import com.example.noteappandroid.entity.NoteLabel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LabelDialogFragment : BaseDialogFragment() {
    private lateinit var binding: FragmentLabelDialogBinding
    private val labelViewModel by viewModels<LabelViewModel>()
    private val noteLabelViewModel by viewModels<NoteLabelViewModel>()
    private val args: LabelDialogFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLabelDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnCreateLabel.setOnClickListener {
            val labelTitle = binding.edtLabelTitle.text.toString()
            val label = Label(0, args.folderId, labelTitle)
            labelViewModel.createLabel(label)
            viewLifecycleOwner.lifecycleScope.launch {
                labelViewModel.newLabelId.collectLatest {
                    Log.d("LabelDialogFragment", "onViewCreated1: $it noteId : ${args.noteId}")
                    if (args.isCreateFromNote && it != -1L) {
                        Log.d("LabelDialogFragment", "onViewCreated2: $it noteId : ${args.noteId}")
                        val noteLabel = NoteLabel(0, args.noteId, it)
                        noteLabelViewModel.createNoteLabel(noteLabel)
                    }
                }
            }
            findNavController().popBackStack()
        }
    }

}