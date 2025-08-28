package com.example.spicestyle

import android.content.Context
import android.content.SharedPreferences

object TokenStore {
    private const val PREF = "spotify_tokens"
    private const val KEY_ACCESS = "access_token"
    private const val KEY_REFRESH = "refresh_token"
    private const val KEY_EXPIRES = "expires_at" // epoch millis

    private fun prefs(ctx: Context): SharedPreferences =
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)

    fun saveTokens(ctx: Context, access: String, refresh: String?, expiresAtMillis: Long) {
        prefs(ctx).edit()
            .putString(KEY_ACCESS, access)
            .putString(KEY_REFRESH, refresh)
            .putLong(KEY_EXPIRES, expiresAtMillis)
            .apply()
    }

    fun accessToken(ctx: Context): String? = prefs(ctx).getString(KEY_ACCESS, null)
    fun refreshToken(ctx: Context): String? = prefs(ctx).getString(KEY_REFRESH, null)
    fun expiresAt(ctx: Context): Long = prefs(ctx).getLong(KEY_EXPIRES, 0L)

    fun clear(ctx: Context) { prefs(ctx).edit().clear().apply() }
}
