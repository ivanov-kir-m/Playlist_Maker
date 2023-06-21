package com.practicum.playlistmaker.activity

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import com.practicum.playlistmaker.R


class SettingsActivity : AppCompatActivity() {

    @SuppressLint("ApplySharedPref")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        //Назад
        findViewById<androidx.appcompat.widget.Toolbar>(
            R.id.asBtnBack
        ).setNavigationOnClickListener {
            finish()
        }

        //Поделиться приложением
        findViewById<TextView>(R.id.asShareBtn).setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, getString(R.string.url_practicum))
                type = "text/plain"
                startActivity(this)
            }
        }

        //Написать в поддержку
        findViewById<TextView>(R.id.asSupportButt).setOnClickListener {
            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_theme_msg))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.support_msg))
                try {
                    startActivity(this)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                    Log.d("Email error:", e.toString())
                }
            }
        }

        //Пользовательское соглашение
        findViewById<TextView>(R.id.asUserAgreeBtn).setOnClickListener{
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string.as_user_agree_url)),
                )
            )
        }

        //Смена темы
        findViewById<SwitchCompat>(R.id.asDarkThemeSwch).setOnCheckedChangeListener {
                _, isChecked ->
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
        }
    }
}