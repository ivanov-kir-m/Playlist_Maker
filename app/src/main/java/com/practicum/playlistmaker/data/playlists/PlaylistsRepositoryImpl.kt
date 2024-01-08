package com.practicum.playlistmaker.data.playlists


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import com.practicum.playlistmaker.data.convertors.PlaylistDbConvertor
import com.practicum.playlistmaker.data.db.AppDatabase
import com.practicum.playlistmaker.data.playlists.db.entity.PlaylistWithTracks
import com.practicum.playlistmaker.domain.IMAGE_QUALITY
import com.practicum.playlistmaker.domain.PLAYLISTS_IMAGE_DIRECTORY
import com.practicum.playlistmaker.domain.player.model.Track
import com.practicum.playlistmaker.domain.playlists.PlaylistsRepository
import com.practicum.playlistmaker.domain.playlists.model.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileOutputStream

class PlaylistsRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val convertor: PlaylistDbConvertor,
    private val context: Context
) : PlaylistsRepository {

    override suspend fun addPlaylist(
        name: String,
        description: String,
        pictureUri: Uri?
    ) {
        appDatabase
            .playlistsDao()
            .addPlaylistEntity(
                convertor.map(
                    Playlist(
                        name = name,
                        description = description,
                        picture = saveImageAndTakeName(pictureUri),
                        tracksList = listOf(),
                        numbersOfTrack = 0,
                    )
                )
            )
    }


    override suspend fun deletePlaylist(playlist: Playlist) {
        appDatabase
            .playlistsDao()
            .deletePlaylistEntity(convertor.map(playlist))
    }


    override fun getAllPlaylist(): Flow<List<Playlist>> {
        return flow {
            val playlists = appDatabase
                .playlistsDao()
                .getPlaylistsWithTracks()
            emit(convertFromPlaylistEntity(playlists))
        }
    }

    private fun saveImageAndTakeName(uri: Uri?): Uri? {
        if (uri == null) return null
        val imageName = System.currentTimeMillis().toString() + ".jpg"
        val filePath = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            PLAYLISTS_IMAGE_DIRECTORY
        )
        //создаем каталог, если он не создан
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        //создаём экземпляр класса File, который указывает на файл внутри каталога
        val file = File(filePath, imageName)
        // создаём входящий поток байтов из выбранной картинки
        val inputStream = context.contentResolver.openInputStream(uri)
        // создаём исходящий поток байтов в созданный выше файл
        val outputStream = FileOutputStream(file)
        // записываем картинку с помощью BitmapFactory
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, outputStream)

        val imageFile = File(filePath, imageName)
        return imageFile.toUri()
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        appDatabase
            .favoritesDao()
            .insertTrackEntity(convertor.map(track))
        appDatabase.playlistsDao().addTrackToPlaylists(
            convertor.map(playlist, track)
        )
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistWithTracks>): List<Playlist> {
        return playlists.map { playlist -> convertor.map(playlist) }
    }
}