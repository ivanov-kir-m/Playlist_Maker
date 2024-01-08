package com.practicum.playlistmaker.ui.playlist_details.state

import android.net.Uri
import com.practicum.playlistmaker.domain.player.model.Track

sealed interface PlaylistsDetailsState {

    data class Info(
        val name: String,
        val description: String,
        val picture: Uri?
    ) : PlaylistsDetailsState

    data class Tracks(
        val tracksList: List<Track>,
        val countTracks: Int,
        val timeTracksMillis: Int,
    ) : PlaylistsDetailsState
}