package com.practicum.playlistmaker.domain.playlists.impl

import android.net.Uri
import com.practicum.playlistmaker.domain.player.model.Track
import com.practicum.playlistmaker.domain.playlists.PlaylistsInteractor
import com.practicum.playlistmaker.domain.playlists.PlaylistsRepository
import com.practicum.playlistmaker.domain.playlists.model.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsInteractorImpl(private val playlistsRepository: PlaylistsRepository) :
    PlaylistsInteractor {

    override suspend fun addPlaylist(name: String, description: String, pictureUri: Uri?) {
        playlistsRepository.addPlaylist(name, description, pictureUri)
    }

    override fun getAllPlaylist(): Flow<List<Playlist>> {
        return playlistsRepository.getAllPlaylist()
    }

    override fun playlistIsAlready(name: String): Flow<Boolean> {
        return playlistsRepository.getAllPlaylist()
            .map { list -> list.any { it.name == name } }
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        playlistsRepository.addTrackToPlaylist(track, playlist)
    }

    override suspend fun deleteTrackFromAllPlaylist(track: Track, playlistId: Int) {
        playlistsRepository.deleteTrackFromAllPlaylist(track, playlistId)
    }

    override suspend fun deletePlaylistById(playlistId: Int) {
        playlistsRepository.deletePlaylistById(playlistId)
    }

    override suspend fun updatePlaylistById(
        playlistId: Int,
        name: String,
        description: String,
        pictureUri: Uri?
    ) {
        playlistsRepository.updatePlaylistById(
            playlistId,
            name,
            description,
            pictureUri
        )
    }

    override suspend fun getPlaylistById(playlistId: Int): Playlist {
        return playlistsRepository.getPlaylistById(playlistId)
    }

    override suspend fun getPlaylistWithTracksById(playlistId: Int): Playlist? {
        return playlistsRepository.getPlaylistWithTracksById(playlistId)
    }
}