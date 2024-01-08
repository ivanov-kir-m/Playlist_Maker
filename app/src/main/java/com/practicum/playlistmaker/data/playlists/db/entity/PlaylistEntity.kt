package com.practicum.playlistmaker.data.playlists.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity
    (
    @PrimaryKey(autoGenerate = true)
    val playlistId: Int = 0,
    val name: String,
    val description: String,
    val pictureName: String?,
)