package com.practicum.playlistmaker.ui.media.view_model

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.playlists.PlaylistsInteractor
import com.practicum.playlistmaker.ui.media.view_model.model.NewPlaylistState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    fun saveData(context: Context, name: String, description: String) {
        viewModelScope.launch {
            playlistsInteractor.addPlaylist(
                name = name,
                description = description,
                pictureUri = _newPlaylistViewState.value?.pictureUri,
            )
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    context.getString(R.string.add_new_playlist_massage, name),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}