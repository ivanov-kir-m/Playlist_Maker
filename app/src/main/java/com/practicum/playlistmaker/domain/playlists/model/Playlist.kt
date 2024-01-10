package com.practicum.playlistmaker.domain.playlists.model

import com.practicum.playlistmaker.domain.player.model.Track
import java.io.Serializable


data class Playlist(
    val playlistId: Int = 0,
    val name: String,
    val description: String,
    val picture: String?,
    val tracksList: List<Track>,
    val numbersOfTrack: Int,
) : Serializable