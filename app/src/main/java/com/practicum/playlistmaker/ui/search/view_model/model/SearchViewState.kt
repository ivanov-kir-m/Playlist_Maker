package com.practicum.playlistmaker.ui.search.view_model.model

import com.practicum.playlistmaker.domain.playlists.model.Playlist

data class SearchViewState(
    val playButtonEnabled: Boolean,
    val playButtonImage: Int,
    val playTextTime: String,
    val favoriteBtn: Boolean,
    val playlists: List<Playlist>,
    val thereTrackInPlaylist: Boolean,
)