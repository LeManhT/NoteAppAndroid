package com.example.noteappandroid.utils

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import com.example.noteappandroid.R
import com.example.noteappandroid.entity.MyColor

fun Context.colorResource(@ColorRes id: Int) = ResourcesCompat.getColor(resources, id, null)
fun Context.stringResource(@StringRes id: Int, vararg formatArgs: Any? = emptyArray()) =
    getString(id, *formatArgs)
fun Context.dimenResource(@DimenRes id: Int) = resources.getDimension(id)
fun Context.fontResource(@FontRes id: Int) = ResourcesCompat.getFont(this, id)

fun MyColor.toColorResourceId(): Int = when (this) {
    MyColor.Blue -> R.color.colorAccentBlue
    MyColor.Gray -> R.color.colorAccentGray
    MyColor.Pink -> R.color.colorAccentPink
    MyColor.Cyan -> R.color.colorAccentCyan
    MyColor.Purple -> R.color.colorAccentPurple
    MyColor.Red -> R.color.colorAccentRed
    MyColor.Yellow -> R.color.colorAccentYellow
    MyColor.Orange -> R.color.colorAccentOrange
    MyColor.Green -> R.color.colorAccentGreen
    MyColor.Brown -> R.color.colorAccentBrown
    MyColor.BlueGray -> R.color.colorAccentBlueGray
    MyColor.Teal -> R.color.colorAccentTeal
    MyColor.Indigo -> R.color.colorAccentIndigo
    MyColor.DeepPurple -> R.color.colorAccentDeepPurple
    MyColor.DeepOrange -> R.color.colorAccentDeepOrange
    MyColor.DeepGreen -> R.color.colorAccentDeepGreen
    MyColor.LightBlue -> R.color.colorAccentLightBlue
    MyColor.LightGreen -> R.color.colorAccentLightGreen
    MyColor.LightRed -> R.color.colorAccentLightRed
    MyColor.LightPink -> R.color.colorAccentLightPink
    MyColor.Black -> R.color.colorAccentBlack
}