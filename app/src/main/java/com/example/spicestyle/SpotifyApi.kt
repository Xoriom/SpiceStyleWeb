package com.example.spicestyle

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class NowPlayingActivity : AppCompatActivity() {

    private lateinit var tokenStore: TokenStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize your token store (make sure you already have TokenStore implemented)
        tokenStore = TokenStore(this)

        // 🔹 Guard against missing token with a Toast
        if (tokenStore.accessToken.isNullOrEmpty()) {
            Toast.makeText(
                this,
                "Please log in first (no token).",
                Toast.LENGTH_LONG
            ).show()
            finish()
            return
        }

        setContentView(R.layout.activity_now_playing)

        // TODO: Continue setting up UI + Spotify API calls here...
    }
}
