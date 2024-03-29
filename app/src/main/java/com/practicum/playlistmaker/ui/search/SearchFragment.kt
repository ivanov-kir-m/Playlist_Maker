package com.practicum.playlistmaker.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.domain.player.model.Track
import com.practicum.playlistmaker.ui.search.adapter.TracksAdapter
import com.practicum.playlistmaker.ui.search.view_model.SearchViewModel
import com.practicum.playlistmaker.ui.search.view_model.model.SearchState
import com.practicum.playlistmaker.utils.Resource
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModel()

    private val trackAdapter = TracksAdapter({ clickOnTrack(it) })
    private val historyTrackAdapter = TracksAdapter({ clickOnTrack(it) })

    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var recyclerViewTrack: RecyclerView
    private lateinit var refreshButtPh: Button
    private lateinit var errorTextPh: TextView
    private lateinit var errorIcnPlaceholder: ImageView
    private lateinit var errorPh: LinearLayout
    private lateinit var clearHistoryButton: Button
    private lateinit var titleHistory: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.stateLiveData.observe(viewLifecycleOwner) {
            showState(it)
        }

        progressBar = binding.progressBar

        recyclerViewTrack = binding.trackSearchRecycler
        recyclerViewTrack.layoutManager = LinearLayoutManager(requireContext())

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

        refreshButtPh = binding.refreshBtn
        refreshButtPh.setOnClickListener {
            viewModel.searchTrackList(inputEditText.text.toString())
        } // реализация кнопки обновить на окне с ошибкой соединения

        errorPh = binding.errorPh
        errorIcnPlaceholder = binding.errorIcnPh
        errorTextPh = binding.errorTextPh

        clearHistoryButton = binding.clearHistoryBtn
        titleHistory = binding.historyTitle

        clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        } // реализация кнопки очистки истории

        inputEditText.requestFocus() // установка фокуса на поисковую строку
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun clickOnTrack(track: Track) {
        if (viewModel.clickDebounce()) {
            viewModel.saveTrackToHistory(track)
            findNavController().navigate(
                R.id.action_searchFragment_to_playerFragment,
                Bundle().apply { putSerializable(TRACK, track) }
            )
        }
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
        val view = requireActivity().currentFocus
        if (view != null) {
            val inputMethodManager =
                requireContext().getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            view.clearFocus()
        }
    }

    private fun showState(stateType: SearchState) {
        when (stateType) {
            is SearchState.Error -> {
                recyclerViewTrack.isVisible = false
                errorPh.isVisible = true
                titleHistory.isVisible = false
                clearHistoryButton.isVisible = false
                progressBar.isVisible = false
                if (stateType.errorMessage == Resource.CONNECTION_ERROR) {
                    errorIcnPlaceholder.setImageResource(R.drawable.icn_no_connection)
                    errorTextPh.setText(R.string.no_connection_msg)
                    refreshButtPh.isVisible = true
                } else {
                    errorIcnPlaceholder.setImageResource(R.drawable.icn_not_found)
                    errorTextPh.setText(R.string.not_found_msg)
                    refreshButtPh.isVisible = false
                }
            }
            is SearchState.Loading -> {
                recyclerViewTrack.isVisible = false
                errorPh.isVisible = false
                refreshButtPh.isVisible = false
                titleHistory.isVisible = false
                clearHistoryButton.isVisible = false
                progressBar.isVisible = true
            }
            is SearchState.SearchResult -> {
                trackAdapter.tracks = stateType.tracks
                recyclerViewTrack.adapter = trackAdapter
                trackAdapter.notifyDataSetChanged()
                recyclerViewTrack.isVisible = true
                errorPh.isVisible = false
                refreshButtPh.isVisible = false
                titleHistory.isVisible = false
                clearHistoryButton.isVisible = false
                progressBar.isVisible = false
            }
            is SearchState.HistoryList -> {
                historyTrackAdapter.tracks = stateType.tracks
                historyTrackAdapter.notifyDataSetChanged()
                recyclerViewTrack.adapter = historyTrackAdapter

                recyclerViewTrack.isVisible = true
                errorPh.isVisible = false
                refreshButtPh.isVisible = false
                titleHistory.isVisible = true
                clearHistoryButton.isVisible = true
                progressBar.isVisible = false
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
