package com.example.noteappandroid.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.noteappandroid.R
import com.example.noteappandroid.entity.Folder
import com.example.noteappandroid.listeners.FolderClickListener

class FolderAdapter(
    private val onFolderClickListener: FolderClickListener? = null
) : RecyclerView.Adapter<FolderViewHolder>() {
    private var listFolder = listOf<Folder>()
    private var selectedFolder: Folder? = null
    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.folder_item, parent, false)
        return FolderViewHolder(view)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folder = listFolder[position]
        holder.titleTextView.text = folder.title
        holder.itemView.setOnClickListener {
            selectedFolder = folder
            selectedPosition = holder.adapterPosition
            notifyDataSetChanged()
            onFolderClickListener?.handleFolderClick(folder)
        }
        holder.ll.setBackgroundColor(
            if (selectedPosition == position) Color.LTGRAY else Color.WHITE
        )
    }

    override fun getItemCount(): Int = listFolder.size

    fun updateData(newListFolder: List<Folder>) {
        this.listFolder = newListFolder
        notifyDataSetChanged()
    }

    fun getSelectedFolder(): Folder? {
        return selectedFolder
    }

    fun setRecyclerViewPosition(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
    }
}

class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val titleTextView: TextView = itemView.findViewById(R.id.txtFolderTitle)
    val ll = itemView.findViewById<LinearLayout>(R.id.ll)
}
