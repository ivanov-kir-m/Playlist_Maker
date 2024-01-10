package com.practicum.playlistmaker.ui.new_playlist.view_model.model

import android.net.Uri
import com.practicum.playlistmaker.utils.Event

data class NewPlaylistState(
    val pictureUri: Uri?,
    val nameTextChanged: Boolean,
    val descriptionTextChanged: Boolean,
    val playlistAlready: Boolean,
    val playlistCreatedEvent: Event<String>?,
)