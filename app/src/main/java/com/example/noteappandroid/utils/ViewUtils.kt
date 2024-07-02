package com.example.noteappandroid.utils

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.core.graphics.ColorUtils
import com.example.noteappandroid.R
import com.example.noteappandroid.entity.MyColor
import com.google.android.material.snackbar.Snackbar

fun Drawable.setRippleColor(colorStateList: ColorStateList) {
    val rippleDrawable = mutate() as RippleDrawable
    rippleDrawable.setColor(colorStateList.withAlpha(32))
}

fun @receiver:ColorInt Int.toColorStateList() = ColorStateList.valueOf(this)


fun @receiver:ColorInt Int.withDefaultAlpha(alpha: Int = 32): Int =
    ColorUtils.setAlphaComponent(this, alpha)

@Suppress("DEPRECATION")
fun View.performClickHapticFeedback() =
    performHapticFeedback(
        HapticFeedbackConstants.VIRTUAL_KEY,
        HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
    )

fun View.snackbar(
    text: String,
    @DrawableRes drawableId: Int? = null,
    @IdRes anchorViewId: Int? = null,
    color: MyColor? = null,
    vibrate: Boolean = true,
) = Snackbar.make(this, text, Snackbar.LENGTH_SHORT).apply {
    animationMode = Snackbar.ANIMATION_MODE_SLIDE
    val textView = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
    if (anchorViewId != null) setAnchorView(anchorViewId)
    if (drawableId != null) {
        textView?.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableId, 0, 0, 0)
        textView?.compoundDrawablePadding = context.dimenResource(R.dimen.spacing_normal).toInt()
        textView?.gravity = Gravity.CENTER
    }
    if (color != null) {
        val backgroundColor = context.getColor(color.toColorResourceId())
        val contentColor = context.colorResource(R.color.colorBackground)
        setBackgroundTint(backgroundColor)
        setTextColor(contentColor)
        textView?.compoundDrawablesRelative?.get(0)?.mutate()?.setTint(contentColor)
    } else {
        val backgroundColor = context.colorResource(R.color.colorPrimary)
        val contentColor = context.colorResource(R.color.colorBackground)
        setBackgroundTint(backgroundColor)
        setTextColor(contentColor)
        textView?.compoundDrawablesRelative?.get(0)?.mutate()?.setTint(contentColor)
    }
    if (vibrate) performClickHapticFeedback()
    show()
}