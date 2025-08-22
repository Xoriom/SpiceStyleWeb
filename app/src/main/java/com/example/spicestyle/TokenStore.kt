package com.example.spicestyle

import android.content.Context
import android.content.SharedPreferences

class TokenStore(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("spotify_tokens", Context.MODE_PRIVATE)

    var accessToken: String?
        get() = prefs.getString("access_token", null)
        set(v) { prefs.edit().putString("access_token", v).apply() }

    var refreshToken: String?
        get() = prefs.getString("refresh_token", null)
        set(v) { prefs.edit().putString("refresh_token", v).apply() }

    var codeVerifier: String?
        get() = prefs.getString("code_verifier", null)
        set(v) { prefs.edit().putString("code_verifier", v).apply() }

    fun clear() { prefs.edit().clear().apply() }
}
