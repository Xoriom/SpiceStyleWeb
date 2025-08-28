package com.example.spicestyle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.spicestyle.ui.theme.SpiceStyleTheme
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SpiceStyleTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(onClick = { openSpotifyLogin() }) {
                            Text(text = getString(R.string.connect_spotify))
                        }
                        Button(
                            modifier = Modifier.padding(top = 16.dp),
                            onClick = {
                                // your existing navigation to NowPlayingActivity if needed
                                // startActivity(Intent(this, NowPlayingActivity::class.java))
                            }
                        ) {
                            Text(text = getString(R.string.open_now_playing))
                        }
                    }
                }
            }
        }
    }

    private fun openSpotifyLogin() {
        val state = UUID.randomUUID().toString()
        val uri = SpotifyAuthManager.buildAuthorizeUri(state)
        val customTabs = CustomTabsIntent.Builder().build()
        customTabs.launchUrl(this, uri)
    }
}
