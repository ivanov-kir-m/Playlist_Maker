package com.practicum.playlistmaker.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.models.Track
import com.practicum.playlistmaker.addappters.TracksAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.view.inputmethod.EditorInfo
import com.practicum.playlistmaker.models.TrackResponse
import com.practicum.playlistmaker.apps.App.Companion.APP_SETTINGS
import com.practicum.playlistmaker.models.SearchHistory
import com.practicum.playlistmaker.utils.ItunesApi

class SearchActivity : AppCompatActivity() {

    companion object{
        const val SEARCH_TEXT = "search_text"
    }

    enum class StateType {
        CONNECTION_ERROR,
        NOT_FOUND,
        SEARCH_RESULT,
        HISTORY_LIST,
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

    private lateinit var searchHistory: SearchHistory
    private lateinit var inputEditText  : EditText
    private lateinit var clearBtn : ImageView
    private lateinit var recyclerViewTrack : RecyclerView
    private lateinit var refreshBtnPh : Button
    private lateinit var errorTextPh : TextView
    private lateinit var errorIcPh : ImageView
    private lateinit var errorPh: LinearLayout
    private lateinit var clearHistoryButton: Button
    private lateinit var titleHistory: TextView

    private fun clearBtnVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun clickOnTrack(track: Track) {
        searchHistory.addTrack(track)
    }

    private val searchTextWatcher = object  : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            clearBtn.visibility = clearBtnVisibility(s)
            searchEditText = s.toString()
            if (inputEditText.hasFocus() && s.isNullOrEmpty() && searchHistory.getList()
                    .isNotEmpty()
            ) showState(StateType.HISTORY_LIST)
            else showState(StateType.SEARCH_RESULT)
        }

        override fun afterTextChanged(s: Editable?) {
            if (inputEditText.hasFocus() && searchHistory.getList().isNotEmpty()) showState(
                StateType.HISTORY_LIST
            )
            else showState(StateType.SEARCH_RESULT)
        }

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
        refreshBtnPh.visibility = View.VISIBLE
        errorIcPh.setImageResource(R.drawable.ic_no_connection)
        errorTextPh.setText(R.string.no_connection_msg)
        titleHistory.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
    }

    private fun onNotFoundError(){
        recyclerViewTrack.visibility = View.GONE
        errorPh.visibility = View.VISIBLE
        refreshBtnPh.visibility = View.GONE
        errorIcPh.setImageResource(R.drawable.ic_not_found)
        errorTextPh.setText(R.string.not_found_msg)
        titleHistory.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
    }

    private fun onSearchResult(){
        trackAdapter.tracks = searchTrackList
        recyclerViewTrack.adapter = trackAdapter
        recyclerViewTrack.visibility = View.VISIBLE
        errorPh.visibility = View.GONE
        refreshBtnPh.visibility = View.GONE
        titleHistory.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
    }

    private fun onShowHistoryList(){
        historyTrackAdapter.tracks = searchHistory.getList()
        recyclerViewTrack.adapter = historyTrackAdapter
        recyclerViewTrack.visibility = View.VISIBLE
        errorPh.visibility = View.GONE
        refreshBtnPh.visibility = View.GONE
        titleHistory.visibility = View.VISIBLE
        clearHistoryButton.visibility = View.VISIBLE
    }

    private fun showState(stateType: StateType){
        when(stateType){
            StateType.CONNECTION_ERROR -> onConnectionError()
            StateType.NOT_FOUND -> onNotFoundError()
            StateType.SEARCH_RESULT -> onSearchResult()
            StateType.HISTORY_LIST -> onShowHistoryList()
        }

    }

    private fun searchTrackList() {
        if (inputEditText.text.isNotEmpty()) {
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
        refreshBtnPh = findViewById(R.id.refresh_btn)
        refreshBtnPh.setOnClickListener{
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
        errorIcPh = findViewById(R.id.error_icn_ph)
        errorTextPh = findViewById(R.id.error_text_ph)
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