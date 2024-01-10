package com.practicum.playlistmaker.data.playlists.db.dao

import androidx.room.*
import com.practicum.playlistmaker.data.favorites.db.entity.TrackEntity
import com.practicum.playlistmaker.data.playlists.db.entity.PlaylistEntity
import com.practicum.playlistmaker.data.playlists.db.entity.PlaylistTracks
import com.practicum.playlistmaker.data.playlists.db.entity.PlaylistWithTracks

@Dao
interface PlaylistsDao {
    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPlaylistEntity(playlistEntity: PlaylistEntity)

    @Update(entity = PlaylistEntity::class)
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Insert(entity = PlaylistTracks::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrackToPlaylists(track: PlaylistTracks)

    @Transaction
    @Query("SELECT * FROM playlist_table")
    suspend fun getPlaylistsWithTracks(): List<PlaylistWithTracks>

    @Query("DELETE FROM PlaylistTracks WHERE trackId = :trackId and playlistId = :playlistId")
    suspend fun deleteTrackFromPlaylist(playlistId: Int, trackId: Int)

    @Query("SELECT * FROM playlist_table WHERE playlistId = :id")
    suspend fun getPlaylistsEntityById(id: Int): PlaylistEntity

    @Query(
        "SELECT tracks_table.* FROM PlaylistTracks " +
                "JOIN tracks_table ON tracks_table.trackId = PlaylistTracks.trackId " +
                "WHERE PlaylistTracks.playlistId=:playlistId " +
                "ORDER BY playlistTrackId DESC"
    )
    suspend fun getAssociatedTracks(playlistId: Int): List<TrackEntity>

    @Query("")
    @Transaction
    suspend fun getPlaylistsWithTracksById(id: Int): PlaylistWithTracks {
        return PlaylistWithTracks(
            getPlaylistsEntityById(id),
            getAssociatedTracks(id)
        )
    }

    @Query("SELECT * FROM playlist_table WHERE playlistId = :id")
    suspend fun getPlaylistsById(id: Int): PlaylistEntity

    @Query("DELETE FROM playlist_table WHERE playlistId = :id")
    suspend fun deletePlaylistById(id: Int)
}