package com.practicum.playlistmaker.utils

import android.media.MediaPlayer
import com.practicum.playlistmaker.data.impl.PlayerRepositoryImpl
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.player.PlayerInteractor
import com.practicum.playlistmaker.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.domain.player.PlayerRepository
import com.practicum.playlistmaker.presentation.player.PlayerPresenter

object Creator {

    fun providePlayerInteractor(track: Track): PlayerInteractor {
        return PlayerInteractorImpl(providePlayerRepository(track))
    }

    private fun providePlayerRepository(track: Track): PlayerRepository {
        return PlayerRepositoryImpl(
            track,
            MediaPlayer()
        )
    }

    fun providePlayerPresenter(track: Track): PlayerPresenter {
        return PlayerPresenter(providePlayerInteractor(track), track)
    }
}
