package com.practicum.playlistmaker.data.playlists.db.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation
import com.practicum.playlistmaker.data.favorites.db.entity.TrackEntity

@Entity(primaryKeys = ["playlistId", "trackId"])
data class PlaylistTracks(
    val playlistId: Int,
    val trackId: Int
)

data class PlaylistWithTracks(
    @Embedded val playlist: PlaylistEntity,
    @Relation(
        parentColumn = "playlistId",
        entityColumn = "trackId",
        associateBy = Junction(PlaylistTracks::class)
    )
    val tracks: List<TrackEntity>
)