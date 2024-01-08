package com.practicum.playlistmaker.ui.new_playlist.view_model.model

import android.net.Uri

data class NewPlaylistState(
    val pictureUri: Uri?,
    val nameTextChanged: Boolean,
    val descriptionTextChanged: Boolean,
    val playlistAlready: Boolean,
)