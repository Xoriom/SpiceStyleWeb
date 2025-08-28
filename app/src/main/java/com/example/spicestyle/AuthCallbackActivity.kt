package com.example.spicestyle

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class AuthCallbackActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data: Uri? = intent?.data
        val code = data?.getQueryParameter("code")
        val error = data?.getQueryParameter("error")

        if (error != null) {
            // Handle error from Spotify (user canceled / denied)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        if (code.isNullOrBlank()) {
            // No code → just return to main
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        lifecycleScope.launch {
            try {
                val token = SpotifyAuthManager.exchangeCode(code)
                // TODO: persist token.access_token (EncryptedSharedPreferences recommended)
            } catch (t: Throwable) {
                // Log/report failure if needed
            } finally {
                startActivity(Intent(this@AuthCallbackActivity, MainActivity::class.java))
                finish()
            }
        }
    }
}
