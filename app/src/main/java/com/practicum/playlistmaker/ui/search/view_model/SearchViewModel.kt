package com.practicum.playlistmaker.ui.search.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.domain.player.model.Track
import com.practicum.playlistmaker.domain.search.SearchInteractor
import com.practicum.playlistmaker.ui.search.view_model.model.SearchState
import com.practicum.playlistmaker.utils.Resource

class SearchViewModel(private val searchInteractor: SearchInteractor) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())

    private var lastSearchText = ""

    private val _stateLiveData = MutableLiveData<SearchState>()
    val stateLiveData: LiveData<SearchState> get() = _stateLiveData

    init {
        getHistoryList()
    }

    fun searchTrackList(query: String) {
        handler.removeCallbacksAndMessages(null)
        if (query.isNotEmpty()) {
            query.let {
                _stateLiveData.postValue(SearchState.Loading)
                searchInteractor.searchTrackList(query, object : SearchInteractor.SearchConsumer {
                    override fun consume(
                        foundTracks: List<Track>?,
                        errorMassage: String?
                    ) {
                        when (errorMassage) {
                            null -> {
                                val tracks = arrayListOf<Track>()
                                if (foundTracks != null) {
                                    tracks.addAll(foundTracks)
                                }
                                _stateLiveData.postValue(SearchState.SearchResult(tracks = tracks))
                            }
                            Resource.NOT_FOUND -> {
                                _stateLiveData.postValue(SearchState.Error(errorMassage))
                            }
                            else -> {
                                _stateLiveData.postValue(SearchState.Error(Resource.CONNECTION_ERROR))
                            }
                        }
                    }
                })
            }
        }
    }

    fun searchDebounce(changedText: String) {
        handler.removeCallbacksAndMessages(null)
        if (lastSearchText != changedText) {
            handler.postDelayed({
                searchTrackList(changedText)
            }, SEARCH_DEBOUNCE_DELAY)
            lastSearchText = changedText
        }
    }

    fun saveTrackToHistory(track: Track) {
        searchInteractor.addTrackToHistory(track)
    }

    fun getHistoryList() {
        if (historyIsNotEmpty())
            _stateLiveData.postValue(
                SearchState.HistoryList(
                    searchInteractor.getHistoryList()
                )
            )
    }

    fun clearHistory() {
        searchInteractor.clearHistoryList()
        _stateLiveData.postValue(SearchState.SearchResult(arrayListOf()))
    }

    private fun historyIsNotEmpty(): Boolean {
        return searchInteractor.getHistoryList().isNotEmpty()
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(null)
    }

    companion object {
        const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

}