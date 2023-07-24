package com.practicum.playlistmaker.activity

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R


class SettingsActivity : AppCompatActivity() {

    private fun setActionBtnBack() {
        //Назад
        findViewById<androidx.appcompat.widget.Toolbar>(
            R.id.asBtnBack
        ).setNavigationOnClickListener {
            finish()
        }
    }

    private fun setActionBtnShare() {
        //Поделиться приложением
        findViewById<TextView>(R.id.asShareBtn).setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, getString(R.string.url_practicum))
                type = "text/plain"
                startActivity(this)
            }
        }
    }

    private fun setActionBtnSupport() {
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
    }

    private fun setActionBtnAgreement() {
        //Пользовательское соглашение
        findViewById<TextView>(R.id.asUserAgreeBtn).setOnClickListener{
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string.as_user_agree_url)),
                )
            )
        }
    }


    private fun initThemeSwitcher() {
        //Смена темы
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.asDarkThemeSwch)
        themeSwitcher.isChecked = (applicationContext as App).darkTheme
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
        }
    }

    @SuppressLint("ApplySharedPref")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setActionBtnBack()
        setActionBtnShare()
        setActionBtnSupport()
        setActionBtnAgreement()
        initThemeSwitcher()
    }
}