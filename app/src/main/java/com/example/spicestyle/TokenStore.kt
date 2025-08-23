package com.example.spicestyle

import android.content.Context
import android.content.SharedPreferences

class TokenStore(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("spotify_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_EXPIRES_AT = "expires_at"
    }

    val accessToken: String?
        get() = prefs.getString(KEY_ACCESS_TOKEN, null)

    val expiresAt: Long
        get() = prefs.getLong(KEY_EXPIRES_AT, 0L)

    fun saveToken(token: String, expiresInSeconds: Long) {
        val expiresAt = System.currentTimeMillis() + (expiresInSeconds * 1000)
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, token)
            .putLong(KEY_EXPIRES_AT, expiresAt)
            .apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    fun isTokenValid(): Boolean {
        val token = accessToken
        val now = System.currentTimeMillis()
        return !token.isNullOrEmpty() && now < expiresAt
    }
}
