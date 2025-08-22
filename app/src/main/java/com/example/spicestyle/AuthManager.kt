package com.example.spicestyle

import android.net.Uri
import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom

object AuthManager {
    const val CLIENT_ID = "a8d8149eaa8c4030bf16fc36eca2d496" // <-- replace
    const val REDIRECT_URI = "spicestyle://callback"
    private const val AUTH_URL = "https://accounts.spotify.com/authorize"
    private const val TOKEN_URL = "https://accounts.spotify.com/api/token"

    // Add/remove scopes as needed
    val SCOPES = listOf(
        "user-read-email",
        "playlist-read-private",
        "user-read-playback-state",
        "user-modify-playback-state"
    ).joinToString(" ")

    fun generateCodeVerifier(): String {
        val bytes = ByteArray(64)
        SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
            .replace("=", "")
    }

    fun codeChallenge(verifier: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(verifier.toByteArray(Charsets.US_ASCII))
        return Base64.encodeToString(digest, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
            .replace("=", "")
    }

    fun buildAuthUri(codeChallenge: String): Uri =
        Uri.parse(AUTH_URL).buildUpon()
            .appendQueryParameter("client_id", CLIENT_ID)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("redirect_uri", REDIRECT_URI)
            .appendQueryParameter("scope", SCOPES)
            .appendQueryParameter("code_challenge_method", "S256")
            .appendQueryParameter("code_challenge", codeChallenge)
            .build()

    fun tokenRequestBodyAuthCode(code: String, verifier: String): Map<String, String> = mapOf(
        "grant_type" to "authorization_code",
        "code" to code,
        "redirect_uri" to REDIRECT_URI,
        "client_id" to CLIENT_ID,
        "code_verifier" to verifier
    )

    fun tokenRequestBodyRefresh(refreshToken: String): Map<String, String> = mapOf(
        "grant_type" to "refresh_token",
        "refresh_token" to refreshToken,
        "client_id" to CLIENT_ID
    )

    const val TOKEN_ENDPOINT = TOKEN_URL
}
