package com.example.spicestyle

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val tokenStore by lazy { TokenStore(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val status = findViewById<TextView>(R.id.status)
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val nowPlayingBtn = findViewById<Button>(R.id.nowPlayingBtn)

        // If already logged in, fetch profile + playlists
        tokenStore.accessToken?.let { token ->
            status.text = "Authorized. Fetching profile…"
            fetchProfileAndPlaylists(status, token)
        }

        // Login flow
        loginBtn.setOnClickListener { startLoginFlow() }

        // Open Now Playing screen
        nowPlayingBtn.setOnClickListener {
            startActivity(Intent(this, NowPlayingActivity::class.java))
        }
    }

    private fun startLoginFlow() {
        val verifier = AuthManager.generateCodeVerifier()
        val challenge = AuthManager.codeChallenge(verifier)
        tokenStore.codeVerifier = verifier

        val authUri: Uri = AuthManager.buildAuthUri(challenge)
        CustomTabsIntent.Builder().build().launchUrl(this, authUri)
    }

    private fun fetchProfileAndPlaylists(status: TextView, accessToken: String) {
        lifecycleScope.launch {
            try {
                val api = SpotifyClient.authed(accessToken)
                val me = api.me()
                val playlists = api.playlists(limit = 1)
                status.text = "Hello, ${me.displayName ?: "Spotify user"}\nPlaylists: ${playlists.total}"
            } catch (t: Throwable) {
                Log.e("Main", "API error: ${t.message}", t)
                status.text = "API error. Try logging in again."
            }
        }
    }
}
