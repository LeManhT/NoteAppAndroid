package com.example.noteappandroid.adapter

import android.graphics.BitmapFactory
import android.graphics.Color
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.noteappandroid.databinding.WidgetNoteItemBinding
import com.example.noteappandroid.entity.NoteEntity

class NoteAdapter : ListAdapter<NoteEntity, NoteAdapter.NotesViewHolder>(diffCallback) {
    private var selectedPosition: Int? = null
    private lateinit var listener: OnItemClickListener

    inner class NotesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = WidgetNoteItemBinding.bind(view)

        fun bind(note: NoteEntity) {
            with(binding) {
                val spannable = Html.fromHtml(note.noteText, Html.FROM_HTML_MODE_LEGACY)

                txtNoteTitle.text = note.title
                txtNoteBody.text = spannable
                txtNoteDate.text = note.dateTime

//                Log.d("TAF 1", "bind: ${note.color}")
                note.color?.let {
                    txtNoteTitle.setTextColor(Color.parseColor(note.color))
                }

                if (note.imgPath.isNullOrEmpty().not()) {
                    imgNote.setImageBitmap(BitmapFactory.decodeFile(note.imgPath))
                    imgNote.visibility = View.VISIBLE
                } else {
                    imgNote.visibility = View.GONE
                }

                if (note.storeWebLink.isNullOrEmpty().not()) {
                    tvWebLink.text = note.storeWebLink
                    tvWebLink.visibility = View.VISIBLE
                } else {
                    tvWebLink.visibility = View.GONE
                }

                cardView.setOnClickListener {
                    note.folderId?.let { it1 -> listener.onClicked(note.id, it1) }
                    imgSelected.visibility = View.GONE
                }

                cardView.setOnLongClickListener {
                    listener.onLongClicked(note)
                    selectedPosition = position
                    notifyDataSetChanged()
                    true
                }

                imgSelected.visibility =
                    if (selectedPosition == position) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val binding =
            WidgetNoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotesViewHolder(binding.root)
    }


    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setOnClickListener(listener1: OnItemClickListener) {
        listener = listener1
    }

    fun clearSelection() {
        selectedPosition = null
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onClicked(noteId: Long, folderId: Long)
        fun onLongClicked(note: NoteEntity)
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<NoteEntity>() {
            override fun areItemsTheSame(oldItem: NoteEntity, newItem: NoteEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: NoteEntity, newItem: NoteEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}