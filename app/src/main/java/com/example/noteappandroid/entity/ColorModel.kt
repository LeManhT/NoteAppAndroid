package com.example.noteappandroid.entity

import android.view.View
import android.view.ViewParent
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.example.noteappandroid.R

data class ColorModel(
    val colorRes: Int
) : EpoxyModelWithHolder<ColorModel.ColorHolder>() {

    override fun getDefaultLayout() = R.layout.color_item
    override fun createNewHolder(parent: ViewParent): ColorHolder {
        return ColorHolder()
    }
    override fun bind(holder: ColorHolder) {
        holder.colorView.setBackgroundResource(colorRes)
    }

    class ColorHolder : EpoxyHolder() {
        lateinit var colorView: View

        override fun bindView(itemView: View) {
            colorView = itemView.findViewById(R.id.imgNoteBlue)
        }
    }
}