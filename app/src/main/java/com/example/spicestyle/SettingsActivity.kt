package com.example.spicestyle

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply current theme BEFORE super
        ThemeManager.apply(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val defaultBtn = findViewById<Button>(R.id.btn_default_theme)
        val icebergBtn = findViewById<Button>(R.id.btn_iceberg_theme)

        defaultBtn.setOnClickListener {
            ThemeManager.setTheme(this, "default")
            recreate()  // rebind with new theme
        }

        icebergBtn.setOnClickListener {
            ThemeManager.setTheme(this, "iceberg")
            recreate()
        }
    }
}
