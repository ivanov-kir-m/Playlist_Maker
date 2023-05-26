package com.practicum.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

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

        val toastAmBtnFindId = Toast.makeText(
            this, "Нажали на amBtnSearch!", Toast.LENGTH_SHORT
        )
        val toastAmBtnMediaId = Toast.makeText(
            this, "Нажали на amBtnMedia!", Toast.LENGTH_LONG
        )

        amBtnSearchId.setOnClickListener {
            toastAmBtnFindId.show()
        }

        amBtnMediaId.setOnClickListener {
            toastAmBtnMediaId.show()
        }

        amBtnSettingsId.setOnClickListener {
            val activitySettingIntentOpen = Intent(this, SettingsActivity::class.java)
            startActivity(activitySettingIntentOpen)
        }

    }
}