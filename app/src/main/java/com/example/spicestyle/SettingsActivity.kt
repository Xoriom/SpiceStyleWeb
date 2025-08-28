package com.example.spicestyle

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.apply(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val defaultBtn = findViewById<Button>(R.id.btn_default_theme)
        val icebergBtn = findViewById<Button>(R.id.btn_iceberg_theme)

        defaultBtn.setOnClickListener {
            ThemeManager.setTheme(this, "default")
            Toast.makeText(this, "Switched to Default", Toast.LENGTH_SHORT).show()
            recreate()
        }

        icebergBtn.setOnClickListener {
            ThemeManager.setTheme(this, "iceberg")
            Toast.makeText(this, "Switched to Iceberg", Toast.LENGTH_SHORT).show()
            recreate()
        }
    }
}
