package com.practicum.playlistmaker

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setListeners()
    }

    private fun setListeners() {
        val amBtnSearchId = findViewById<Button>(R.id.amBtnSearch)
        val amBtnMediaId = findViewById<Button>(R.id.amBtnMedia)
        val amBtnSettingsId = findViewById<Button>(R.id.amBtnSettings)

        amBtnSearchId.setOnClickListener {
            onButtonClick(SearchActivity::class.java)
        }

        amBtnMediaId.setOnClickListener {
            onButtonClick(MediaActivity::class.java)
        }

        amBtnSettingsId.setOnClickListener {
            onButtonClick(SettingsActivity::class.java)
        }

    }

    private fun onButtonClick(targetClass: Class<out Activity>) {
        val intent = Intent(this, targetClass)
        startActivity(intent)
    }
}