package com.practicum.playlistmaker.ui.player.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.*
import com.practicum.playlistmaker.domain.player.PlayerInteractor
import com.practicum.playlistmaker.ui.search.view_model.model.SearchViewState
import com.practicum.playlistmaker.utils.DateUtils.millisToStrFormat

class PlayerViewModel(private val player: PlayerInteractor) : ViewModel() {

    private val _searchViewState = MutableLiveData(
        // Это дефолтный стейт экрана, который будет применён сразу после открытия
        SearchViewState(
            playButtonEnabled = false,
            playButtonImage = R.drawable.icn_play,
            playTextTime = millisToStrFormat(START_PLAY_TIME_MILLIS)
        )
    )

    val searchViewState: LiveData<SearchViewState> get() = _searchViewState

    private var mainThreadHandler = Handler(Looper.getMainLooper())

    init {
        conditionPlayButton()
    }

    fun prepareTrack(url: String) {
        if (player.getPlayerState() == STATE_DEFAULT) {
            player.prepareTrack(url)
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
        }
    }

    private fun startPlayer() {
        player.startPlayer()
        // Обновление стейта
        _searchViewState.value = _searchViewState.value?.copy(
            playButtonImage = R.drawable.icn_pause
        )
        updateTimeAndButton()
    }

    fun pausePlayer() {
        player.pausePlayer()
        _searchViewState.value = _searchViewState.value?.copy(
            playButtonImage = R.drawable.icn_play
        )
        mainThreadHandler.removeCallbacksAndMessages(null)
    }

    private fun updateTimeAndButton() {
        var lastCurrentTime = REFRESH_PLAY_TIME_MILLIS
        _searchViewState.value = _searchViewState.value?.copy(
            playTextTime = millisToStrFormat(player.getCurrentTime())
        )
        mainThreadHandler.postDelayed(
            object : Runnable {
                override fun run() {
                    val currentTime = player.getCurrentTime()
                    if (currentTime < REFRESH_PLAY_TIME_MILLIS && lastCurrentTime != currentTime) {
                        lastCurrentTime = currentTime
                        _searchViewState.value = _searchViewState.value?.copy(
                            playTextTime = millisToStrFormat(currentTime)
                        )
                    } else {
                        lastCurrentTime = REFRESH_PLAY_TIME_MILLIS
                        _searchViewState.value = _searchViewState.value?.copy(
                            playButtonImage = R.drawable.icn_play,
                            playTextTime = millisToStrFormat(START_PLAY_TIME_MILLIS)
                        )
                    }
                    // И снова планируем то же действие через 0.5 сек
                    mainThreadHandler.postDelayed(
                        this,
                        DELAY_MILLIS
                    )
                }
            }, DELAY_MILLIS
        )
    }

    private fun conditionPlayButton() {
        mainThreadHandler.postDelayed(
            object : Runnable {
                override fun run() {
                    _searchViewState.value = _searchViewState.value?.copy(
                        playButtonEnabled = player.getPlayerState() != STATE_DEFAULT
                    )
                    mainThreadHandler.postDelayed(this, DELAY_MILLIS)
                }
            }, DELAY_MILLIS
        )
    }

    override fun onCleared() {
        super.onCleared()
        mainThreadHandler.removeCallbacksAndMessages(null)
        player.releasePlayer()
    }
}