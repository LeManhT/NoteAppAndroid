package com.example.noteappandroid.utils

import android.view.View

object Utils {
    const val EMPTY_STRING = ""

    fun View.makeVisible() {
        this.visibility = View.VISIBLE
    }

    fun View.makeGone() {
        this.visibility = View.GONE
    }

    fun makeVisible(vararg views: View) {
        for (view in views) {
            view.visibility = View.VISIBLE
        }
    }

    fun makeGone(vararg views: View) {
        for (view in views) {
            view.visibility = View.GONE
        }
    }
}