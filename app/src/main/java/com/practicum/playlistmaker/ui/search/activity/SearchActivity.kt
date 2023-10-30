package com.practicum.playlistmaker.ui.search.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.domain.CLICK_ITEM_DELAY
import com.practicum.playlistmaker.domain.player.model.Track
import com.practicum.playlistmaker.ui.player.activity.PlayerActivity
import com.practicum.playlistmaker.ui.search.adapter.TracksAdapter
import com.practicum.playlistmaker.ui.search.view_model.SearchViewModel
import com.practicum.playlistmaker.ui.search.view_model.model.SearchState
import com.practicum.playlistmaker.utils.Resource
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by viewModel()

    private val trackAdapter = TracksAdapter { clickOnTrack(it) }
    private val historyTrackAdapter = TracksAdapter { clickOnTrack(it) }

    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var recyclerViewTrack: RecyclerView
    private lateinit var refreshButtPh: Button
    private lateinit var errorTextPh: TextView
    private lateinit var errorIcPh: ImageView
    private lateinit var errorPh: LinearLayout
    private lateinit var clearHistoryButton: Button
    private lateinit var titleHistory: TextView
    private lateinit var progressBar: ProgressBar
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.stateLiveData.observe(this) {
            showState(it)
        }

        progressBar = binding.progressBar

        recyclerViewTrack = binding.trackSearchRecycler
        recyclerViewTrack.layoutManager = LinearLayoutManager(this)

        inputEditText = binding.inputEditText

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                if (inputEditText.hasFocus() && s.isNullOrEmpty()) {
                    viewModel.getHistoryList()
                }
                viewModel.searchDebounce(inputEditText.text.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        inputEditText.addTextChangedListener(searchTextWatcher)
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.searchTrackList(inputEditText.text.toString())
                true
            }
            false
        } // производит поиск при нажатии на кнопку DONE на клавиатуре

        clearButton = binding.clearIcon
        clearButton.visibility = clearButtonVisibility(inputEditText.text)
        clearButton.setOnClickListener {
            clearSearch()
        } // реализация кнопки очисти поисковой стоки

        binding.asBtnBack.setNavigationOnClickListener {
            finish()
        } // реализация кнопки назад

        refreshButtPh = binding.refreshBtn
        refreshButtPh.setOnClickListener {
            viewModel.searchTrackList(inputEditText.text.toString())
        } // реализация кнопки обновить на окне с ошибкой соединения

        errorPh = binding.errorPh
        errorIcPh = binding.errorIcnPh
        errorTextPh = binding.errorTextPh

        clearHistoryButton = binding.clearHistoryBtn
        titleHistory = binding.historyTitle

        clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        } // реализация кнопки очистки истории

        inputEditText.requestFocus() // установка фокуса на поисковую строку
    }

    private fun clickOnTrack(track: Track) {
        if (clickDebounce()) {
            viewModel.saveTrackToHistory(track)
            val playerIntent = Intent(this, PlayerActivity::class.java).apply {
                putExtra(TRACK, track)
            }
            startActivity(playerIntent)
        }
    }

    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_ITEM_DELAY)
        }
        return current
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun clearSearch() {
        inputEditText.setText("")
        trackAdapter.notifyDataSetChanged()
        val view = this.currentFocus
        if (view != null) {
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            view.clearFocus()
        }
    }

    private fun showState(stateType: SearchState) {
        when (stateType) {
            is SearchState.Error -> {
                recyclerViewTrack.visibility = View.GONE
                errorPh.visibility = View.VISIBLE
                titleHistory.visibility = View.GONE
                clearHistoryButton.visibility = View.GONE
                progressBar.visibility = View.GONE
                if (stateType.errorMessage == Resource.CONNECTION_ERROR) {
                    errorIcPh.setImageResource(R.drawable.icn_no_connection)
                    errorTextPh.setText(R.string.no_connection_msg)
                    refreshButtPh.visibility = View.VISIBLE
                } else {
                    errorIcPh.setImageResource(R.drawable.icn_not_found)
                    errorTextPh.setText(R.string.not_found_msg)
                    refreshButtPh.visibility = View.GONE
                }
            }
            is SearchState.Loading -> {
                recyclerViewTrack.visibility = View.GONE
                errorPh.visibility = View.GONE
                refreshButtPh.visibility = View.GONE
                titleHistory.visibility = View.GONE
                clearHistoryButton.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
            }
            is SearchState.SearchResult -> {
                trackAdapter.tracks = stateType.tracks
                recyclerViewTrack.adapter = trackAdapter
                trackAdapter.notifyDataSetChanged()
                recyclerViewTrack.visibility = View.VISIBLE
                errorPh.visibility = View.GONE
                refreshButtPh.visibility = View.GONE
                titleHistory.visibility = View.GONE
                clearHistoryButton.visibility = View.GONE
                progressBar.visibility = View.GONE
            }
            is SearchState.HistoryList -> {
                historyTrackAdapter.tracks = stateType.tracks
                historyTrackAdapter.notifyDataSetChanged()
                recyclerViewTrack.adapter = historyTrackAdapter

                recyclerViewTrack.visibility = View.VISIBLE
                errorPh.visibility = View.GONE
                refreshButtPh.visibility = View.GONE
                titleHistory.visibility = View.VISIBLE
                clearHistoryButton.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }
        }

    }

    override fun onStart() {
        super.onStart()
        if (viewModel.stateLiveData.value is SearchState.HistoryList)
            viewModel.getHistoryList()
    }

    companion object {
        const val TRACK = "TRACK"
    }
}
