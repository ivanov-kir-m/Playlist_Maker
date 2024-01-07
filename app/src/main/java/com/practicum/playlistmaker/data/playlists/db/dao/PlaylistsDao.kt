package com.practicum.playlistmaker.data.playlists.db.dao

import androidx.room.*
import com.practicum.playlistmaker.data.playlists.db.entity.PlaylistEntity
import com.practicum.playlistmaker.data.playlists.db.entity.PlaylistTracks
import com.practicum.playlistmaker.data.playlists.db.entity.PlaylistWithTracks

@Dao
interface PlaylistsDao {
    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPlaylistEntity(playlistEntity: PlaylistEntity)

    @Delete(entity = PlaylistEntity::class)
    suspend fun deletePlaylistEntity(playlistEntity: PlaylistEntity)

    @Query("SELECT * FROM playlist_table")
    suspend fun getAllPlaylist(): List<PlaylistEntity>

    @Update(entity = PlaylistEntity::class)
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Insert(entity = PlaylistTracks::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrackToPlaylists(track: PlaylistTracks)

    @Transaction
    @Query("SELECT * FROM playlist_table")
    suspend fun getPlaylistsWithTracks(): List<PlaylistWithTracks>
}