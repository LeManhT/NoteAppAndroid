package com.example.noteappandroid.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.noteappandroid.R
import com.example.noteappandroid.entity.PhotoSplash

class CircleIndicatorAdapter(private val listItemsSlash: List<PhotoSplash>) :
    RecyclerView.Adapter<CircleIndicatorAdapter.CircleIndicatorViewHolder>() {
    class CircleIndicatorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CircleIndicatorViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_photo_splash, parent, false)
        return CircleIndicatorViewHolder(view)
    }

    override fun onBindViewHolder(holder: CircleIndicatorViewHolder, position: Int) {
        holder.itemView.apply {
            val txtSplash = findViewById<TextView>(R.id.txtSplash)
            val imgPhoto = findViewById<ImageView>(R.id.img_photo)

            txtSplash.text = listItemsSlash[position].textSplash
            imgPhoto.setImageResource(listItemsSlash[position].resourceId)
        }
    }

    override fun getItemCount(): Int {
        return listItemsSlash.size
    }
}