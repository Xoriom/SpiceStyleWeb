package com.example.spicestyle

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class AuthCallbackActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data: Uri? = intent?.data
        if (data == null) {
            finish()
            return
        }

        val mgr = SpotifyAuthManager(this)
        lifecycleScope.launch {
            val ok = mgr.handleRedirect(data)
            Toast.makeText(
                this@AuthCallbackActivity,
                if (ok) "Spotify connected!" else "Spotify auth failed",
                Toast.LENGTH_SHORT
            ).show()

            // Go back to Main
            startActivity(Intent(this@AuthCallbackActivity, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP))
            finish()
        }
    }
}
