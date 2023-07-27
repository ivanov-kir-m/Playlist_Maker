package com.practicum.playlistmaker.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setListeners()
    }

    private fun onButtonClick(targetClass: Class<out Activity>) {
        val intent = Intent(this, targetClass)
        startActivity(intent)
    }

    private fun setListeners() {
        val searchBtnId = findViewById<Button>(R.id.searchBtn)
        val mediaBtn = findViewById<Button>(R.id.mediaBtn)
        val settingsBtn = findViewById<Button>(R.id.settingsBtn)

        searchBtnId.setOnClickListener {
            onButtonClick(SearchActivity::class.java)
        }

        mediaBtn.setOnClickListener {
            onButtonClick(MediaActivity::class.java)
        }

        settingsBtn.setOnClickListener {
            onButtonClick(SettingsActivity::class.java)
        }

    }
}