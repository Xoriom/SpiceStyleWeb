package com.example.spicestyle

import android.app.Activity
import android.content.Context

// Old constants your code refers to
const val THEME_DEFAULT = "default"
const val THEME_DRIBBLISH_OCEAN = "iceberg"
const val THEME_DRIBBLISH_GLACIER = "iceberg"
const val THEME_DRIBBLISH_AURORA = "iceberg"

// Old helper names → delegate to ThemeManager
fun applyTheme(activity: Activity) {
    ThemeManager.apply(activity)
}

fun saveTheme(context: Context, value: String) {
    ThemeManager.setTheme(context, value)
}
