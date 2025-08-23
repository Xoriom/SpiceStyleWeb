package com.example.spicestyle

import android.content.Context

object ThemeManager {
    private const val PREFS = "theme_prefs"
    private const val KEY = "selected_theme" // "default" or "iceberg"

    fun apply(context: Context) {
        val mode = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY, "default")
        when (mode) {
            "iceberg" -> context.setTheme(R.style.Theme_SpiceStyle_Iceberg)
            else      -> context.setTheme(R.style.Theme_SpiceStyle_Default)
        }
    }

    fun setTheme(context: Context, value: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY, value).apply()
    }
}
