package com.practicum.playlistmaker.data.playlists.db.entity

import androidx.room.*
import com.practicum.playlistmaker.data.favorites.db.entity.TrackEntity

@Entity()
data class PlaylistTracks(
    @PrimaryKey(autoGenerate = true)
    val playlistTrackId: Int = 0,
    val playlistId: Int,
    val trackId: Int
)

data class PlaylistWithTracks(
    @Embedded val playlist: PlaylistEntity,
    @Relation(
        entity = TrackEntity::class,
        parentColumn = "playlistId",
        entityColumn = "trackId",
        associateBy = Junction(PlaylistTracks::class)
    )
    val tracks: List<TrackEntity>,

//    @Relation(
//        parentColumn = "trackId",
//        entityColumn = "playlistTrackId",
//        associateBy = Junction(PlaylistTracks::class)
//    )
//    val tracksIndexes: List<PlaylistTracks>
)