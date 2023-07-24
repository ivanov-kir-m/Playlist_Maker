package com.practicum.playlistmaker.activity

import ItunesApi
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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

class SearchActivity : AppCompatActivity() {

    companion object{
        const val SEARCH_TEXT = "search_text"
    }

    enum class StateType {
        CONNECTION_ERROR, NOT_FOUND, SEARCH_RESULT
    }

    private var searchEditText : String = ""

    private val itunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesService = retrofit.create(ItunesApi::class.java)

    private val searchTrackList = ArrayList<Track>()
    private val trackAdapter = TracksAdapter(searchTrackList)

    private lateinit var inputEditText  : EditText
    private lateinit var clearBtn : ImageView
    private lateinit var recyclerViewTrack : RecyclerView
    private lateinit var refreshButtPh : Button
    private lateinit var errorTextPh : TextView
    private lateinit var errorIcPh : ImageView
    private lateinit var errorPh: LinearLayout

    private fun clearBtnVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private val searchTextWatcher = object  : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            clearBtn.visibility = clearBtnVisibility(s)
            searchEditText = s.toString()
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
        refreshButtPh.visibility = View.VISIBLE
        errorIcPh.setImageResource(R.drawable.ic_no_connection)
        errorTextPh.setText(R.string.no_connection_msg)
    }

    private fun onNotFoundError(){
        recyclerViewTrack.visibility = View.GONE
        errorPh.visibility = View.VISIBLE
        refreshButtPh.visibility = View.GONE
        errorIcPh.setImageResource(R.drawable.ic_not_found)
        errorTextPh.setText(R.string.not_found_msg)
    }

    private fun onNSearchResult(){
        recyclerViewTrack.visibility = View.VISIBLE
        errorPh.visibility = View.GONE
        refreshButtPh.visibility = View.GONE
    }

    private fun showState(stateType: StateType){
        when(stateType){
            StateType.CONNECTION_ERROR -> onConnectionError()
            StateType.NOT_FOUND -> onNotFoundError()
            StateType.SEARCH_RESULT -> onNSearchResult()
        }

    }

    private fun searchTrackList() {
        if (inputEditText.text.isNotEmpty()) {
            Log.i("search","Search ${inputEditText.text}")
            itunesService.search(inputEditText.text.toString())
                .enqueue(object : Callback<TrackResponse> {
                    override fun onResponse(
                        call: Call<TrackResponse>,
                        response: Response<TrackResponse>
                    ) {
                        Log.i("search","Resp code ${response.code()}")
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
        recyclerViewTrack = findViewById<RecyclerView>(R.id.trackSearchRecycler)
        recyclerViewTrack.layoutManager = LinearLayoutManager(this)
        recyclerViewTrack.adapter = trackAdapter
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
    }

    private fun initClearBtn() {
        clearBtn = findViewById(R.id.clearIcon)
        clearBtn.visibility = clearBtnVisibility(inputEditText.text)
        clearBtn.setOnClickListener {
            clearSearch()
        }
    }

    private fun initRefreshBtn() {
        refreshButtPh = findViewById(R.id.refresh_butt)
        refreshButtPh.setOnClickListener{
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
        initRecycler()
        initSearchInput()
        initClearBtn()
        initRefreshBtn()
        setActionBtnBack()

        errorIcPh = findViewById(R.id.error_icn_ph)
        errorTextPh = findViewById(R.id.error_text_ph)
        errorPh = findViewById(R.id.error_ph)

    }
}