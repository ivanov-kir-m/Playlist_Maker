package com.practicum.playlistmaker.ui.search.view_model.model

import androidx.lifecycle.MutableLiveData

data class SearchViewState(
    val playButtonEnabled: MutableLiveData<Boolean> = MutableLiveData<Boolean>(),
    val playButtonImage: MutableLiveData<Int> = MutableLiveData<Int>(),
    val playTextTime: MutableLiveData<String> = MutableLiveData<String>(),
)