package com.example.noteappandroid.ui.create_notes

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.navArgs
import com.example.noteappandroid.R
import com.example.noteappandroid.databinding.FragmentNoteBottomSheetBinding
import com.example.noteappandroid.utils.Utils.makeGone
import com.example.noteappandroid.utils.Utils.makeVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NoteBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNoteBottomSheetBinding
    private var selectedColor = "#3e434e"
    private val args: NoteBottomSheetFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        noteId = args.noteId
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (noteId != null) {
            binding.layoutDeleteNote.makeVisible()
        } else {
            binding.layoutDeleteNote.makeGone()
        }
        setListener()

    }

    companion object {
        var noteId: Long? = null
    }

    @SuppressLint("RestrictedApi", "InflateParams")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)

        val view = LayoutInflater.from(context).inflate(R.layout.fragment_note_bottom_sheet, null)
        dialog.setContentView(view)

        val param = (view.parent as View).layoutParams as CoordinatorLayout.LayoutParams

        val behavior = param.behavior

        if (behavior is BottomSheetBehavior<*>) {

            behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    BottomSheetBehavior.STATE_DRAGGING.apply { }
                    BottomSheetBehavior.STATE_SETTLING.apply { }
                    BottomSheetBehavior.STATE_EXPANDED.apply { }
                    BottomSheetBehavior.STATE_COLLAPSED.apply { }
                    BottomSheetBehavior.STATE_HIDDEN.apply {
                        dismiss()
                        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                }
                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        }
    }

    private fun setListener() {

        binding.apply {

            fNoteBlue.setOnClickListener {

                imgNoteBlue.setImageResource(R.drawable.donecheck)
                imgNoteCyan.setImageResource(0)
                imgNoteGreen.setImageResource(0)
                imgNoteOrange.setImageResource(0)
                imgNotePurple.setImageResource(0)
                imgNoteRed.setImageResource(0)
                imgNoteYellow.setImageResource(0)
                imgNoteBrown.setImageResource(0)
                imgNoteIndigo.setImageResource(0)
                selectedColor = "#2196f3"

                val intent = Intent(getString(R.string.bottom_sheet_action))
                intent.putExtra(getString(R.string.action), getString(R.string.blue))
                intent.putExtra(getString(R.string.selected_color), selectedColor)
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
            }

            fNoteCyan.setOnClickListener {

                imgNoteBlue.setImageResource(0)
                imgNoteCyan.setImageResource(R.drawable.donecheck)
                imgNoteGreen.setImageResource(0)
                imgNoteOrange.setImageResource(0)
                imgNotePurple.setImageResource(0)
                imgNoteRed.setImageResource(0)
                imgNoteYellow.setImageResource(0)
                imgNoteBrown.setImageResource(0)
                imgNoteIndigo.setImageResource(0)
                selectedColor = "#00e5ff"

                val intent = Intent(getString(R.string.bottom_sheet_action))
                intent.putExtra(getString(R.string.action), getString(R.string.cyan))
                intent.putExtra(getString(R.string.selected_color), selectedColor)
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
            }

            fNoteGreen.setOnClickListener {

                imgNoteBlue.setImageResource(0)
                imgNoteCyan.setImageResource(0)
                imgNoteGreen.setImageResource(R.drawable.donecheck)
                imgNoteOrange.setImageResource(0)
                imgNotePurple.setImageResource(0)
                imgNoteRed.setImageResource(0)
                imgNoteYellow.setImageResource(0)
                imgNoteBrown.setImageResource(0)
                imgNoteIndigo.setImageResource(0)
                selectedColor = "#00c853"

                val intent = Intent(getString(R.string.bottom_sheet_action))
                intent.putExtra(getString(R.string.action), getString(R.string.green))
                intent.putExtra(getString(R.string.selected_color), selectedColor)
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
            }

            fNoteOrange.setOnClickListener {

                imgNoteBlue.setImageResource(0)
                imgNoteCyan.setImageResource(0)
                imgNoteGreen.setImageResource(0)
                imgNoteOrange.setImageResource(R.drawable.donecheck)
                imgNotePurple.setImageResource(0)
                imgNoteRed.setImageResource(0)
                imgNoteYellow.setImageResource(0)
                imgNoteBrown.setImageResource(0)
                imgNoteIndigo.setImageResource(0)
                selectedColor = "#ff6d00"

                val intent = Intent(getString(R.string.bottom_sheet_action))
                intent.putExtra(getString(R.string.action), getString(R.string.orange))
                intent.putExtra(getString(R.string.selected_color), selectedColor)
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
            }

            fNotePurple.setOnClickListener {

                imgNoteBlue.setImageResource(0)
                imgNoteCyan.setImageResource(0)
                imgNoteGreen.setImageResource(0)
                imgNoteOrange.setImageResource(0)
                imgNotePurple.setImageResource(R.drawable.donecheck)
                imgNoteRed.setImageResource(0)
                imgNoteYellow.setImageResource(0)
                imgNoteBrown.setImageResource(0)
                imgNoteIndigo.setImageResource(0)
                selectedColor = "#aa00ff"

                val intent = Intent(getString(R.string.bottom_sheet_action))
                intent.putExtra(getString(R.string.action), getString(R.string.purple))
                intent.putExtra(getString(R.string.selected_color), selectedColor)
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
            }

            fNoteRed.setOnClickListener {

                imgNoteBlue.setImageResource(0)
                imgNoteCyan.setImageResource(0)
                imgNoteGreen.setImageResource(0)
                imgNoteOrange.setImageResource(0)
                imgNotePurple.setImageResource(0)
                imgNoteRed.setImageResource(R.drawable.donecheck)
                imgNoteYellow.setImageResource(0)
                imgNoteBrown.setImageResource(0)
                imgNoteIndigo.setImageResource(0)
                selectedColor = "#d50000"

                val intent = Intent(getString(R.string.bottom_sheet_action))
                intent.putExtra(getString(R.string.action), getString(R.string.red))
                intent.putExtra(getString(R.string.selected_color), selectedColor)
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
            }

            fNoteYellow.setOnClickListener {

                imgNoteBlue.setImageResource(0)
                imgNoteCyan.setImageResource(0)
                imgNoteGreen.setImageResource(0)
                imgNoteOrange.setImageResource(0)
                imgNotePurple.setImageResource(0)
                imgNoteRed.setImageResource(0)
                imgNoteYellow.setImageResource(R.drawable.donecheck)
                imgNoteBrown.setImageResource(0)
                imgNoteIndigo.setImageResource(0)
                selectedColor = "#ffeb3b"

                val intent = Intent(getString(R.string.bottom_sheet_action))
                intent.putExtra(getString(R.string.action), getString(R.string.yellow))
                intent.putExtra(getString(R.string.selected_color), selectedColor)
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
            }

            fNoteBrown.setOnClickListener {

                imgNoteBlue.setImageResource(0)
                imgNoteCyan.setImageResource(0)
                imgNoteGreen.setImageResource(0)
                imgNoteOrange.setImageResource(0)
                imgNotePurple.setImageResource(0)
                imgNoteRed.setImageResource(0)
                imgNoteYellow.setImageResource(0)
                imgNoteBrown.setImageResource(R.drawable.donecheck)
                imgNoteIndigo.setImageResource(0)
                selectedColor = "#3e2723"

                val intent = Intent(getString(R.string.bottom_sheet_action))
                intent.putExtra(getString(R.string.action), getString(R.string.brown))
                intent.putExtra(getString(R.string.selected_color), selectedColor)
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
            }

            fNoteIndigo.setOnClickListener {

                imgNoteBlue.setImageResource(0)
                imgNoteCyan.setImageResource(0)
                imgNoteGreen.setImageResource(0)
                imgNoteOrange.setImageResource(0)
                imgNotePurple.setImageResource(0)
                imgNoteRed.setImageResource(0)
                imgNoteYellow.setImageResource(0)
                imgNoteBrown.setImageResource(0)
                imgNoteIndigo.setImageResource(R.drawable.donecheck)
                selectedColor = "#1a237e"

                val intent = Intent(getString(R.string.bottom_sheet_action))
                intent.putExtra(getString(R.string.action), getString(R.string.indigo))
                intent.putExtra(getString(R.string.selected_color), selectedColor)
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
            }
            // FINISH COLORS

            // ADD IMAGE
            layoutImage.setOnClickListener {
                val intent = Intent(getString(R.string.bottom_sheet_action))
                intent.putExtra(getString(R.string.action), getString(R.string.image))
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
                dismiss()
            }

            // ADD URL
            layoutWebUrl.setOnClickListener {
                val intent = Intent(getString(R.string.bottom_sheet_action))
                intent.putExtra(getString(R.string.action), getString(R.string.webUrl))
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
                dismiss()
            }

            layoutDeleteNote.setOnClickListener {
                val intent = Intent(getString(R.string.bottom_sheet_action))
                intent.putExtra(getString(R.string.action), getString(R.string.deleteNote))
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
                dismiss()
            }
        }
    }

}