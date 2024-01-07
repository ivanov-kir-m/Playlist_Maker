package com.practicum.playlistmaker.data.convertors

import androidx.core.net.toUri
import com.practicum.playlistmaker.data.db.AppDatabase
import com.practicum.playlistmaker.data.favorites.db.entity.TrackEntity
import com.practicum.playlistmaker.data.playlists.db.entity.PlaylistEntity
import com.practicum.playlistmaker.data.playlists.db.entity.PlaylistTracks
import com.practicum.playlistmaker.data.playlists.db.entity.PlaylistWithTracks
import com.practicum.playlistmaker.domain.player.model.Track
import com.practicum.playlistmaker.domain.playlists.model.Playlist

class PlaylistDbConvertor(
    private val appDatabase: AppDatabase,
) {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            name = playlist.name,
            description = playlist.description,
            pictureName = playlist.picture.toString(),
        )
    }

    fun map(playlist_tracks: PlaylistWithTracks): Playlist {
        val tracksList = playlist_tracks.tracks.map { track -> this.map(track) }
        return Playlist(
            playlist_tracks.playlist.playlistId,
            playlist_tracks.playlist.name,
            playlist_tracks.playlist.description,
            playlist_tracks.playlist.pictureName?.toUri(),
            tracksList,
            tracksList.size,
        )
    }

    fun map(playlist: Playlist, track: Track): PlaylistTracks {
        return PlaylistTracks(
            playlist.playlistId,
            track.trackId,
        )
    }

    fun map(track: TrackEntity): Track {
        return Track(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            track.isFavorite,
        )
    }

    fun map(track: Track): TrackEntity {
        return TrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            track.isFavorite,
        )
    }
}