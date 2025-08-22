package com.example.spicestyle

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

        // If we already have a token, show profile + playlists
        val token = tokenStore.accessToken
        if (token != null) {
            status.text = "Authorized. Fetching profile…"
            fetchProfileAndPlaylists(status, token)
        }

        loginBtn.setOnClickListener {
            startLoginFlow()
        }
    }

    private fun startLoginFlow() {
        // 1) PKCE verifier & challenge
        val verifier = AuthManager.generateCodeVerifier()
        val challenge = AuthManager.codeChallenge(verifier)
        tokenStore.codeVerifier = verifier

        // 2) Launch CustomTab to authorize
        val authUri: Uri = AuthManager.buildAuthUri(challenge)
        CustomTabsIntent.Builder().build().launchUrl(this, authUri)
    }

    private fun fetchProfileAndPlaylists(status: TextView, accessToken: String) {
        lifecycleScope.launch {
            try {
                val api = SpotifyClient.authed(accessToken)
                val me = api.me()
                val playlists = api.playlists(limit = 1) // just to get total
                status.text = "Hello, ${me.displayName ?: "Spotify user"}\nPlaylists: ${playlists.total}"
            } catch (t: Throwable) {
                Log.e("Main", "API error: ${t.message}", t)
                status.text = "API error. Try logging in again."
            }
        }
    }
}
