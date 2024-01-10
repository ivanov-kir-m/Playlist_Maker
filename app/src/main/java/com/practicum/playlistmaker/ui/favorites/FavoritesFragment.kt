package com.practicum.playlistmaker.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavouritesBinding
import com.practicum.playlistmaker.domain.player.model.Track
import com.practicum.playlistmaker.ui.favorites.view_model.FavoritesViewModel
import com.practicum.playlistmaker.ui.search.SearchFragment
import com.practicum.playlistmaker.ui.search.adapter.TracksAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private val viewModel: FavoritesViewModel by viewModel()

    private var _binding: FragmentFavouritesBinding? = null
    private val binding get() = _binding!!

    private var adapter: TracksAdapter? = null
    private lateinit var placeholderMessage: TextView

    private lateinit var placeholderImage: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var favoritesList: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        adapter = null
        favoritesList.adapter = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TracksAdapter({ clickOnTrack(it) })
        favoritesList = binding.favoritesList
        favoritesList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        favoritesList.adapter = adapter

        placeholderMessage = binding.errorTextPh
        placeholderImage = binding.icnPlaceholder
        progressBar = binding.progressBar

        viewModel.fillData()

        viewModel.stateLiveData.observe(viewLifecycleOwner) {
            render(it)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.fillData()
    }

    private fun clickOnTrack(track: Track) {
        findNavController().navigate(
            R.id.action_mediaLibraryFragment_to_playerFragment,
            Bundle().apply { putSerializable(SearchFragment.TRACK, track) }
        )
    }

    private fun render(state: FavoritesState) {
        when (state) {
            is FavoritesState.Content -> showContent(state.tracks)
            is FavoritesState.Empty -> showEmpty()
            is FavoritesState.Loading -> showLoading()
        }
    }

    private fun showLoading() {
        favoritesList.isVisible = false
        placeholderMessage.isVisible = false
        placeholderImage.isVisible = false
        progressBar.isVisible = true
    }

    private fun showEmpty() {
        favoritesList.isVisible = false
        placeholderMessage.isVisible = true
        placeholderImage.isVisible = true
        progressBar.isVisible = false

        placeholderMessage.text = getString(R.string.media_library_empty)
    }

    private fun showContent(tracks: List<Track>) {
        favoritesList.isVisible = true
        placeholderMessage.isVisible = false
        placeholderImage.isVisible = false
        progressBar.isVisible = false

        adapter?.tracks?.clear()
        adapter?.tracks?.addAll(tracks)
        adapter?.invertList()
    }

    companion object {

        fun newInstance() = FavoritesFragment()

    }
}