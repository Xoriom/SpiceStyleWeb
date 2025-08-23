package com.example.spicestyle

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply theme first
        ThemeManager.apply(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val nowBtn = findViewById<MaterialButton>(R.id.btn_now_playing)
        val settingsBtn = findViewById<MaterialButton>(R.id.btn_settings)

        nowBtn.setOnClickListener {
            Toast.makeText(this, "Opening Now Playing…", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, NowPlayingActivity::class.java))
        }

        settingsBtn.setOnClickListener {
            Toast.makeText(this, "Opening Settings…", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}
