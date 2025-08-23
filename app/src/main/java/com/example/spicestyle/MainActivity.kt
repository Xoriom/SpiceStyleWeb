package com.example.spicestyle

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply selected theme BEFORE super
        ThemeManager.apply(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)  // has Settings entry
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            // Optional: quick theme switcher right from Main
            else -> super.onOptionsItemSelected(item)
        }
    }

    /** Optional helper to show a quick theme picker dialog (if you want it here). */
    private fun showThemePicker() {
        val items = arrayOf("Default", "Iceberg")
        AlertDialog.Builder(this)
            .setTitle("Choose Theme")
            .setItems(items) { _, which ->
                when (which) {
                    0 -> ThemeManager.setTheme(this, "default")
                    1 -> ThemeManager.setTheme(this, "iceberg")
                }
                recreate()
            }
            .show()
    }
}
