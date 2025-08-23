package com.example.spicestyle

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.apply(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        findViewById<Button>(R.id.btn_default_theme).setOnClickListener {
            ThemeManager.setTheme(this, "default"); recreate()
        }
        findViewById<Button>(R.id.btn_iceberg_theme).setOnClickListener {
            ThemeManager.setTheme(this, "iceberg"); recreate()
        }
    }
}
