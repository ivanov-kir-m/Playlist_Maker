package com.practicum.playlistmaker.presentation.ui.search

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.search.TracksAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.view.inputmethod.EditorInfo
import com.google.gson.Gson
import com.practicum.playlistmaker.data.dto.TrackResponse
import com.practicum.playlistmaker.presentation.App.Companion.APP_SETTINGS
import com.practicum.playlistmaker.SearchHistory
import com.practicum.playlistmaker.data.network.ItunesApi
import com.practicum.playlistmaker.presentation.ui.player.PlayerActivity

class SearchActivity : AppCompatActivity() {

    companion object{
        const val SEARCH_TEXT = "search_text"
        const val TRACK = "TRACK"
        private const val CLICK_ITEM_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    enum class StateType {
        CONNECTION_ERROR,
        NOT_FOUND,
        SEARCH_RESULT,
        HISTORY_LIST,
        SEARCH_PROGRESS,
    }

    private var searchEditText : String = ""

    private val itunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesService = retrofit.create(ItunesApi::class.java)

    private val searchTrackList = ArrayList<Track>()
    private val trackAdapter = TracksAdapter { clickOnTrack(it) }
    private val historyTrackAdapter = TracksAdapter { clickOnTrack(it) }

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    private val searchRunnable = Runnable { searchTrackList() }

    private lateinit var searchHistory: SearchHistory
    private lateinit var inputEditText  : EditText
    private lateinit var clearBtn : ImageView
    private lateinit var recyclerViewTrack : RecyclerView
    private lateinit var refreshBtn : Button
    private lateinit var errorText : TextView
    private lateinit var errorIcn : ImageView
    private lateinit var errorPh: LinearLayout
    private lateinit var clearHistoryButton: Button
    private lateinit var titleHistory: TextView
    private lateinit var progressBar: ProgressBar

    private fun clearBtnVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_ITEM_DELAY)
        }
        return current
    }

    private fun clickOnTrack(track: Track) {
        if (clickDebounce()) {
            searchHistory.addTrack(track)
            val playerIntent = Intent(this, PlayerActivity::class.java)
            playerIntent.putExtra(TRACK, Gson().toJson(track))
            startActivity(playerIntent)
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private val searchTextWatcher = object  : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            clearBtn.visibility = clearBtnVisibility(s)
            searchEditText = s.toString()
            if (inputEditText.hasFocus() && s.isNullOrEmpty() && searchHistory.getList()
                    .isNotEmpty()
            ) showState(StateType.HISTORY_LIST)
            else searchDebounce()
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchEditText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchEditText = savedInstanceState.getString(SEARCH_TEXT).toString()
        inputEditText.setText(searchEditText)

    }

    private fun onConnectionError(){
        recyclerViewTrack.visibility = View.GONE
        errorPh.visibility = View.VISIBLE
        refreshBtn.visibility = View.VISIBLE
        errorIcn.setImageResource(R.drawable.icn_no_connection)
        errorText.setText(R.string.no_connection_msg)
        titleHistory.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
        progressBar.visibility = View.GONE
    }

    private fun onNotFoundError(){
        recyclerViewTrack.visibility = View.GONE
        errorPh.visibility = View.VISIBLE
        refreshBtn.visibility = View.GONE
        errorIcn.setImageResource(R.drawable.icn_not_found)
        errorText.setText(R.string.not_found_msg)
        titleHistory.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
        progressBar.visibility = View.GONE
    }

    private fun onSearchResult(){
        trackAdapter.tracks = searchTrackList
        recyclerViewTrack.adapter = trackAdapter
        recyclerViewTrack.visibility = View.VISIBLE
        errorPh.visibility = View.GONE
        refreshBtn.visibility = View.GONE
        titleHistory.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
        progressBar.visibility = View.GONE
    }

    private fun onShowHistoryList(){
        historyTrackAdapter.tracks = searchHistory.getList()
        recyclerViewTrack.adapter = historyTrackAdapter
        recyclerViewTrack.visibility = View.VISIBLE
        errorPh.visibility = View.GONE
        refreshBtn.visibility = View.GONE
        titleHistory.visibility = View.VISIBLE
        clearHistoryButton.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }

    private fun onSearchProgress(){
        recyclerViewTrack.visibility = View.GONE
        errorPh.visibility = View.GONE
        refreshBtn.visibility = View.GONE
        titleHistory.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    private fun showState(stateType: StateType){
        when(stateType){
            StateType.CONNECTION_ERROR -> onConnectionError()
            StateType.NOT_FOUND -> onNotFoundError()
            StateType.SEARCH_RESULT -> onSearchResult()
            StateType.HISTORY_LIST -> onShowHistoryList()
            StateType.SEARCH_PROGRESS -> onSearchProgress()
        }

    }

    private fun searchTrackList() {
        if (inputEditText.text.isNotEmpty()) {
            showState(StateType.SEARCH_PROGRESS)
            itunesService.search(inputEditText.text.toString())
                .enqueue(object : Callback<TrackResponse> {
                    override fun onResponse(
                        call: Call<TrackResponse>,
                        response: Response<TrackResponse>
                    ) {
                        if (response.code() == 200) {
                            searchTrackList.clear()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                searchTrackList.addAll(response.body()?.results!!)
                                trackAdapter.notifyDataSetChanged()
                                showState(StateType.SEARCH_RESULT)
                            } else showState(StateType.NOT_FOUND)
                        } else showState(StateType.CONNECTION_ERROR)
                    }

                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                        showState(StateType.CONNECTION_ERROR) //показывать плейсхолдер с ошибкой
                    }
                })
        }
    }

    private fun clearSearch() {
        inputEditText.setText("")
        searchTrackList.clear()
        trackAdapter.notifyDataSetChanged()
        val view = this.currentFocus
        if (view != null) {
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            view.clearFocus()
        }
    }

    private fun initRecycler() {
        recyclerViewTrack = findViewById(R.id.trackSearchRecycler)
        recyclerViewTrack.layoutManager = LinearLayoutManager(this)
    }

    private fun initSearchInput() {
        inputEditText = findViewById(R.id.inputEditText)
        inputEditText.addTextChangedListener(searchTextWatcher)
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTrackList()
                true
            }
            false
        }
        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (searchHistory.getList().isNotEmpty() && hasFocus) showState(StateType.HISTORY_LIST)
        }
        inputEditText.requestFocus()
    }

    private fun initClearBtn() {
        clearBtn = findViewById(R.id.clearIcon)
        clearBtn.visibility = clearBtnVisibility(inputEditText.text)
        clearBtn.setOnClickListener {
            clearSearch()
        }
    }

    private fun initSearchHistory() {
        searchHistory = SearchHistory(getSharedPreferences(APP_SETTINGS, MODE_PRIVATE))
    }

    private fun initClearHistoryBtn() {
        clearHistoryButton = findViewById(R.id.clear_history_btn)
        clearHistoryButton.setOnClickListener {
            searchHistory.clearList()
            showState(StateType.SEARCH_RESULT)
        }
    }

    private fun initRefreshBtn() {
        refreshBtn = findViewById(R.id.refresh_btn)
        refreshBtn.setOnClickListener{
            searchTrackList()
        }
    }

    private fun setActionBtnBack() {
        findViewById<androidx.appcompat.widget.Toolbar>(
            R.id.asBtnBack
        ).setNavigationOnClickListener {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        progressBar = findViewById(R.id.progressBar)
        errorIcn = findViewById(R.id.error_icn_ph)
        errorText = findViewById(R.id.error_text_ph)
        errorPh = findViewById(R.id.error_ph)
        titleHistory = findViewById(R.id.history_title)
        initSearchHistory()
        initRefreshBtn()
        initRecycler()
        initClearHistoryBtn()
        initSearchInput()
        initClearBtn()
        setActionBtnBack()
    }
}