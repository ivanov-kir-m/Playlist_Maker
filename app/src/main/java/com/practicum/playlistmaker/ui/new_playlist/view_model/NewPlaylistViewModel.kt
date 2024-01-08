package com.practicum.playlistmaker.ui.new_playlist.view_model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.playlists.PlaylistsInteractor
import com.practicum.playlistmaker.ui.new_playlist.view_model.model.NewPlaylistState
import com.practicum.playlistmaker.utils.Event
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val _newPlaylistViewState = MutableLiveData(
        NewPlaylistState(
            pictureUri = null,
            nameTextChanged = false,
            descriptionTextChanged = false,
            playlistAlready = false,
        )
    )
    val newPlaylistViewState: LiveData<NewPlaylistState> get() = _newPlaylistViewState

    private val _playlistCreatedEvent = MutableLiveData<Event<String>>()
    val playlistCreatedEvent: LiveData<Event<String>>
        get() = _playlistCreatedEvent

    fun onPlaylistCreated(name: String) {
        _playlistCreatedEvent.value = Event(name)
    }

    fun playlistIsAlready(name: String) {
        viewModelScope.launch {
            _newPlaylistViewState.value = _newPlaylistViewState.value?.copy(
                playlistAlready = playlistsInteractor.playlistIsAlready(name).first()
            )
        }
    }

    fun setNameTextChanged(value: Boolean) {
        viewModelScope.launch {
            _newPlaylistViewState.value = _newPlaylistViewState.value?.copy(
                nameTextChanged = value
            )
        }
    }

    fun setDescriptionTextChanged(value: Boolean) {
        viewModelScope.launch {
            _newPlaylistViewState.value = _newPlaylistViewState.value?.copy(
                descriptionTextChanged = value
            )
        }
    }

    fun setPictureUri(value: Uri?) {
        viewModelScope.launch {
            _newPlaylistViewState.value = _newPlaylistViewState.value?.copy(
                pictureUri = value
            )
        }
    }

    fun createNewPlaylist(name: String, description: String) {
        viewModelScope.launch {
            playlistsInteractor.addPlaylist(
                name = name,
                description = description,
                pictureUri = _newPlaylistViewState.value?.pictureUri,
            )
            onPlaylistCreated(name)
        }
    }

    fun updatePlaylist(
        id: Int,
        newName: String,
        newDescription: String,
        onResult: () -> Unit
    ) {

        viewModelScope.launch {
            playlistsInteractor.updatePlaylistById(
                id,
                newName,
                newDescription,
                _newPlaylistViewState.value?.pictureUri,
            )
            onResult()
        }
    }
}