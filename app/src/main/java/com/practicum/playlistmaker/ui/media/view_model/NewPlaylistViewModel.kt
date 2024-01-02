package com.practicum.playlistmaker.ui.media.view_model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.playlists.PlaylistsInteractor
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val _playlistAlready = MutableLiveData<Boolean>()
    val playlistAlready: LiveData<Boolean> get() = _playlistAlready

    fun playlistIsAlready(name: String) {
        viewModelScope.launch {
            _playlistAlready.value = playlistsInteractor.playlistIsAlready(name).first()
        }
    }

    fun saveData(name: String, description: String, pictureUri: Uri?) {
        viewModelScope.launch {
            playlistsInteractor.addPlaylist(
                name = name,
                description = description,
                pictureUri = pictureUri
            )
        }
    }

}