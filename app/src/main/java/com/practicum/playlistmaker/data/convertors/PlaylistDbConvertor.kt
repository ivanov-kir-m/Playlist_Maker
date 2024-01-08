package com.practicum.playlistmaker.data.convertors

import com.practicum.playlistmaker.data.favorites.db.entity.TrackEntity
import com.practicum.playlistmaker.data.playlists.db.entity.PlaylistEntity
import com.practicum.playlistmaker.data.playlists.db.entity.PlaylistTracks
import com.practicum.playlistmaker.data.playlists.db.entity.PlaylistWithTracks
import com.practicum.playlistmaker.domain.player.model.Track
import com.practicum.playlistmaker.domain.playlists.model.Playlist

class PlaylistDbConvertor {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistId = playlist.playlistId,
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
            playlist_tracks.playlist.pictureName,
            tracksList,
            tracksList.size,
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            playlist.playlistId,
            playlist.name,
            playlist.description,
            playlist.pictureName,
            listOf(),
            0,
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
            track.artworkUrl60,
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
            track.artworkUrl60,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            track.isFavorite,
        )
    }
}