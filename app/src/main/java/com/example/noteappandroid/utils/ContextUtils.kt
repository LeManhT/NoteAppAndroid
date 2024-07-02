package com.example.noteappandroid.utils

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.view.Window
import androidx.core.view.WindowInsetsControllerCompat
import com.example.noteappandroid.R

fun Context.applyNightModeConfiguration(window: Window) {
    val insetsController = WindowInsetsControllerCompat(window, window.decorView.rootView)
    val currentConfiguration = resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)
    if (currentConfiguration == Configuration.UI_MODE_NIGHT_YES) {
        // For API 21 and above
        insetsController.isAppearanceLightStatusBars = false

        // For API 27 and above
        insetsController.isAppearanceLightNavigationBars = false
    } else if (currentConfiguration == Configuration.UI_MODE_NIGHT_NO) {
        // For API 21 and above
        insetsController.isAppearanceLightStatusBars = true

        // For API 27 and above
        insetsController.isAppearanceLightNavigationBars = true
    }
}

fun Context.applySystemBarsColors(window: Window, applyDefaults: Boolean = true) {
    if (applyDefaults) {
        window.statusBarColor = resources.getColor(R.color.colorBackground)
        window.navigationBarColor = resources.getColor(R.color.colorBackground)
    }
}

