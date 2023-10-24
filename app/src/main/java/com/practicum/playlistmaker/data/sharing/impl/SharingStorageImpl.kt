package com.practicum.playlistmaker.data.sharing.impl

import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.sharing.SharingStorage
import com.practicum.playlistmaker.domain.sharing.model.EmailData

class SharingStorageImpl(private val context: Context) : SharingStorage {

    override fun getShareAppLink(): String {
        return context.getString(R.string.url_practicum)
    }

    override fun getTermsLink(): String {
        return context.getString(R.string.as_user_agree_url)
    }

    override fun getSupportEmailData(): EmailData {
        return EmailData(
            email = context.getString(R.string.support_email),
            subject = context.getString(R.string.support_theme_msg),
            text = context.getString(R.string.support_msg),
        )
    }
}
