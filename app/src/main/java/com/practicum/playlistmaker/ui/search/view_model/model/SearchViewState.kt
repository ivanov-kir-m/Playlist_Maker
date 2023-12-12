package com.practicum.playlistmaker.ui.search.view_model.model

data class SearchViewState(
    val playButtonEnabled: Boolean,
    val playButtonImage: Int,
    val playTextTime: String,
    val favoriteBtn: Boolean,
)