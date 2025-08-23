package com.example.spicestyle

import android.app.Activity
import android.content.Context

object ThemeManager {
    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_THEME = "current_theme"

    const val THEME_DEFAULT = R.style.Theme_SpiceStyle
    const val THEME_DRIBBLISH_OCEAN = R.style.Theme_SpiceStyle_DribblishOcean
    const val THEME_DRIBBLISH_GLACIER = R.style.Theme_SpiceStyle_DribblishGlacier
    const val THEME_DRIBBLISH_AURORA = R.style.Theme_SpiceStyle_DribblishAurora

    fun applyTheme(activity: Activity) {
        activity.setTheme(getSavedTheme(activity))
    }

    fun saveTheme(context: Context, theme: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_THEME, theme).apply()
    }

    fun getSavedTheme(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_THEME, THEME_DEFAULT)
    }
}
