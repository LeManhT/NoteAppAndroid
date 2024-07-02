package com.example.noteappandroid.ui.folder

import com.airbnb.epoxy.EpoxyController
import com.example.noteappandroid.entity.ColorModel

class ColorController : EpoxyController() {
    var colors: List<Int> = emptyList()

    override fun buildModels() {
        colors.forEach { colorRes ->
            ColorModel(colorRes)
                .id(colorRes)
                .addTo(this)
        }
    }
}