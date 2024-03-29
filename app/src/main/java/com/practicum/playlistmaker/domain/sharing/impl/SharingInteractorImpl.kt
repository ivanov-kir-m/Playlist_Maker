package com.practicum.playlistmaker.domain.sharing.impl

import com.practicum.playlistmaker.domain.sharing.SharingInteractor
import com.practicum.playlistmaker.domain.sharing.SharingRepository

class SharingInteractorImpl(
    private val sharingRepository: SharingRepository,

    ) : SharingInteractor {
    override fun shareApp() {
        sharingRepository.shareLink()
    }

    override fun openTerms() {
        sharingRepository.openLink()
    }

    override fun openSupport() {
        sharingRepository.openEmail()
    }
}
