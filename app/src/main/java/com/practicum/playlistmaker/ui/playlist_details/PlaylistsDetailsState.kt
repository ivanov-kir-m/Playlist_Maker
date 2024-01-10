package com.practicum.playlistmaker.ui.playlist_details.state

import android.net.Uri
import com.practicum.playlistmaker.domain.player.model.Track

data class PlaylistsDetailsState(
    val name: String,
    val description: String,
    val picture: Uri?,
    val tracksList: List<Track>,
    val countTracks: Int,
    val timeTracksMillis: Int,
)