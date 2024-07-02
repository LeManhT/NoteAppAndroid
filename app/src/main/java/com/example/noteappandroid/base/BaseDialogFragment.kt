package com.example.noteappandroid.base

import android.app.Dialog
import android.os.Bundle
import com.example.noteappandroid.R
import com.example.noteappandroid.utils.applyNightModeConfiguration
import com.example.noteappandroid.utils.applySystemBarsColors
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseDialogFragment(private val isCollapsable: Boolean = false) :
    BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.BottomSheetDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme).apply {
            if (isCollapsable) {
                behavior.peekHeight = 500
            } else {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.let { window ->
            context?.applyNightModeConfiguration(window)
            context?.applySystemBarsColors(window, applyDefaults = false)
        }
    }
}