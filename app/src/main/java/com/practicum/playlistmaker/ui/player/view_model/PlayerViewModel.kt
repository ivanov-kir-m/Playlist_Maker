package com.practicum.playlistmaker.ui.player.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.*
import com.practicum.playlistmaker.domain.player.PlayerInteractor
import com.practicum.playlistmaker.domain.player.model.Track
import com.practicum.playlistmaker.ui.search.view_model.model.SearchViewState
import com.practicum.playlistmaker.utils.DateUtils.millisToStrFormat
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(private val player: PlayerInteractor) : ViewModel() {

    private val _searchViewState = MutableLiveData(
        // Это дефолтный стейт экрана, который будет применён сразу после открытия
        SearchViewState(
            playButtonEnabled = false,
            playButtonImage = R.drawable.icn_play,
            playTextTime = millisToStrFormat(START_PLAY_TIME_MILLIS),
            favoriteBtn = false
        )
    )

    val searchViewState: LiveData<SearchViewState> get() = _searchViewState

    private var timerJob: Job? = null
    private lateinit var track: Track

    private fun trackIsFavorite() {
        viewModelScope.launch {
            player
                .trackIsFavorite(track)
                .collect { isFavorite ->
                    _searchViewState.value = _searchViewState.value?.copy(
                        favoriteBtn = isFavorite
                    )
                }
        }
    }

    fun favoriteButtonFunction() {
        when (track.isFavorite) {
            true -> deleteFavoriteTrack()
            false -> addTrackToFavorites()
        }
    }

    private fun deleteFavoriteTrack() {
        track = track.copy(isFavorite = false)
        viewModelScope.launch {
            player.deleteFavoriteTrack(track)
        }
        _searchViewState.value = _searchViewState.value?.copy(
            favoriteBtn = false
        )
    }

    private fun addTrackToFavorites() {
        track = track.copy(isFavorite = true)
        viewModelScope.launch {
            player.addTrackToFavorites(track)
        }
        _searchViewState.value = _searchViewState.value?.copy(
            favoriteBtn = true
        )
    }

    fun prepareTrack(track: Track) {
        this.track = track
        trackIsFavorite()
        if (player.getPlayerState() == STATE_DEFAULT) {
            player.prepareTrack(track.previewUrl)
            _searchViewState.value = _searchViewState.value?.copy(
                playButtonEnabled = true
            )
        }
    }

    fun playbackControl() {
        when (player.getPlayerState()) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
            else -> {}
        }
    }

    private fun startPlayer() {
        player.startPlayer()
        // Обновление стейта
        _searchViewState.value = _searchViewState.value?.copy(
            playButtonImage = R.drawable.icn_pause
        )
        startTimer()
    }

    fun pausePlayer() {
        player.pausePlayer()
        _searchViewState.value = _searchViewState.value?.copy(
            playButtonImage = R.drawable.icn_play
        )
        timerJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        player.releasePlayer()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (player.getPlayerState() == STATE_PLAYING) {
                delay(DELAY_MILLIS)
                _searchViewState.value = _searchViewState.value?.copy(
                    playTextTime = millisToStrFormat(player.getCurrentTime())
                )
            }
            _searchViewState.value = _searchViewState.value?.copy(
                playButtonImage = R.drawable.icn_play,
                playTextTime = millisToStrFormat(START_PLAY_TIME_MILLIS)
            )
        }
    }
}