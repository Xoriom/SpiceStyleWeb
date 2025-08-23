package com.example.spicestyle

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val tokenStore by lazy { TokenStore(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applyTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val status = findViewById<TextView>(R.id.status)
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val nowPlayingBtn = findViewById<Button>(R.id.nowPlayingBtn)
        val themeBtn = findViewById<Button>(R.id.themeBtn)

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

        // Theme selection menu
        themeBtn.setOnClickListener { showThemeMenu(it) }
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

    private fun showThemeMenu(anchor: View) {
        val popup = PopupMenu(this, anchor)
        popup.menu.add(0, ThemeManager.THEME_DEFAULT, 0, "Default")
        popup.menu.add(0, ThemeManager.THEME_DRIBBLISH_OCEAN, 1, "Dribblish Ocean")
        popup.menu.add(0, ThemeManager.THEME_DRIBBLISH_GLACIER, 2, "Dribblish Glacier")
        popup.menu.add(0, ThemeManager.THEME_DRIBBLISH_AURORA, 3, "Dribblish Aurora")
        popup.setOnMenuItemClickListener { item ->
            ThemeManager.saveTheme(this, item.itemId)
            recreate()
            true
        }
        popup.show()
    }
}
