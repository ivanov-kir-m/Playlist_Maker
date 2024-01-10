package com.practicum.playlistmaker.ui.playlist_details.view_model

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.player.model.Track
import com.practicum.playlistmaker.domain.playlists.PlaylistsInteractor
import com.practicum.playlistmaker.domain.playlists.model.Playlist
import com.practicum.playlistmaker.ui.playlist_details.state.PlaylistsDetailsState
import kotlinx.coroutines.launch

class PlaylistDetailsViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val _stateLiveData = MutableLiveData<PlaylistsDetailsState>()
    val stateLiveData: LiveData<PlaylistsDetailsState> = _stateLiveData

    private var playlist: Playlist? = null

    fun showPlayList(playlistId: Int) {
        getPlaylistWithTracksById(playlistId) {
            showInfo()
        }
    }

    private fun renderState(state: PlaylistsDetailsState) {
        _stateLiveData.postValue(state)
    }

    private fun timeTracksMillis(list: List<Track>): Int {
        var millis = 0
        list.forEach { track -> millis += track.trackTimeMillis }
        return millis
    }

    fun deleteTrack(track: Track) {
        viewModelScope.launch {
            playlist?.playlistId?.let {
                playlistsInteractor.deleteTrackFromAllPlaylist(track, it)
                playlist = playlistsInteractor.getPlaylistWithTracksById(it)
                showInfo()
            }
        }
    }

    private fun showInfo() {
        renderState(
            PlaylistsDetailsState(
                name = playlist!!.name,
                description = playlist!!.description,
                picture = Uri.parse(playlist!!.picture),
                tracksList = playlist!!.tracksList,
                countTracks = playlist!!.tracksList.size,
                timeTracksMillis = timeTracksMillis(playlist!!.tracksList)
            )
        )
    }

    fun deletePlaylist(onResultListener: () -> Unit) {
        viewModelScope.launch {
            playlist?.playlistId?.let {
                Log.e("qwe", "$it")
                playlistsInteractor.deletePlaylistById(it)
                onResultListener()
            }
        }
    }

    private fun getPlaylistWithTracksById(
        playListId: Int,
        onResultListener: () -> Unit
    ) {
        viewModelScope.launch {
            val playlistWithTracks = playlistsInteractor.getPlaylistWithTracksById(playListId)
            if (playlistWithTracks != null) {
                playlist = playlistWithTracks
                onResultListener()
            }
        }
    }
}