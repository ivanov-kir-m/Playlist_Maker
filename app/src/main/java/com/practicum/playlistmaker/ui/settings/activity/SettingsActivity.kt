package com.practicum.playlistmaker.ui.settings.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.ui.settings.view_model.SettingsViewModel
import com.practicum.playlistmaker.ui.settings.view_model.SettingsViewModelFactory

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            SettingsViewModelFactory(this)
        )[SettingsViewModel::class.java]

        // Бинд кнопки назад на тулбаре
        binding.asBtnBack.apply {
            setNavigationOnClickListener {
                finish()
            }
        }

        //Бинд поделитьсчя приложением
        binding.asShareBtn.apply {
            setOnClickListener {
                viewModel.shareApp()
            }
        }

        //Бинд написапть в поддержку
        binding.asSupportBtn.apply {
            setOnClickListener {
                viewModel.openSupport()
            }
        }

        //Бинд откртия пользовательского соглашения
        binding.asUserAgreeBtn.apply {
            setOnClickListener {
                viewModel.openTerms()
            }
        }

        //Бинд свитчера темы
        binding.asDarkThemeSwch.apply {
            isChecked = viewModel.isCheckedTheme()
            setOnCheckedChangeListener { _, isChecked -> viewModel.switchTheme(isChecked) }
        }
    }
}
