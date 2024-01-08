package com.practicum.playlistmaker.domain.playlists.model

import android.net.Uri
import com.practicum.playlistmaker.domain.player.model.Track


data class Playlist(
    val playlistId: Int = 0,
    val name: String,
    val description: String,
    val picture: Uri?,
    val tracksList: List<Track>,
    val numbersOfTrack: Int,
)