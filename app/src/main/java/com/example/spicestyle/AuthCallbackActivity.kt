package com.example.spicestyle

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * Handles the OAuth redirect from Spotify (or any web auth callback).
 * Make sure your manifest has an intent-filter for the redirect URI scheme.
 */
class AuthCallbackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply selected theme before super
        ThemeManager.apply(this)
        super.onCreate(savedInstanceState)

        // Handle the deep link (if any)
        val data: Uri? = intent?.data
        if (data != null) {
            // Example: parse your access token from the redirect (adapt to your flow)
            // e.g., myapp://callback#access_token=...&token_type=Bearer&expires_in=3600
            val fragment = data.fragment.orEmpty()
            val params = fragment.split("&").associate {
                val parts = it.split("=")
                parts.getOrNull(0).orEmpty() to parts.getOrNull(1).orEmpty()
            }
            val token = params["access_token"]
            if (!token.isNullOrEmpty()) {
                TokenStore(this).accessToken = token
                Toast.makeText(this, "Logged in", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
            }
        }

        // Return to main screen
        startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finish()
    }
}
