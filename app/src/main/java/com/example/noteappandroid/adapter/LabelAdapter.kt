package com.example.noteappandroid.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.noteappandroid.R
import com.example.noteappandroid.entity.Label

class LabelAdapter(private val onLabelClickListener: ILabelClick? = null) :
    RecyclerView.Adapter<LabelAdapter.LabelViewHolder>() {
    private var labels: List<Label> = listOf()
    private var selectedPosition: Int? = null

    interface ILabelClick {
        fun handleLabelClick(label: Label)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.label_item, parent, false)
        return LabelViewHolder(view)
    }

    override fun onBindViewHolder(holder: LabelViewHolder, position: Int) {
        val label = labels[position]
        holder.tvTitle.text = label.title
        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position

            // Notify changes for the previous and current selection
            previousPosition?.let { notifyItemChanged(it) }
            notifyItemChanged(position)
            onLabelClickListener?.handleLabelClick(label)
        }
        if (position == selectedPosition) {
            holder.itemView.setBackgroundColor(Color.BLUE)
        } else {
            holder.itemView.setBackgroundColor(Color.GRAY)
        }

//            tvTitle.setBackgroundColor(Color.parseColor(label.color))
    }

    override fun getItemCount(): Int = labels.size

    class LabelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.txt_label)
    }

    fun updateLabels(newLabels: List<Label>) {
        this.labels = newLabels
        notifyDataSetChanged()
    }

}