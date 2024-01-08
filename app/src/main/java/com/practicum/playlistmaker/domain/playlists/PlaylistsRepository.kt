package com.practicum.playlistmaker.domain.playlists

import android.net.Uri
import com.practicum.playlistmaker.domain.player.model.Track
import com.practicum.playlistmaker.domain.playlists.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {

    suspend fun addPlaylist(name: String, description: String, pictureUri: Uri?)

    suspend fun deletePlaylist(playlist: Playlist)

    fun getAllPlaylist(): Flow<List<Playlist>>

    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)

    suspend fun deleteTrackFromAllPlaylist(track: Track, playlistId: Int)

    suspend fun deletePlaylistById(playlistId: Int)

    suspend fun updatePlaylistById(
        playlistId: Int,
        name: String,
        description: String,
        pictureUri: Uri?
    )

    suspend fun getPlaylistById(playlistId: Int): Playlist

    suspend fun getPlaylistWithTracksById(playlistId: Int): Playlist?


}