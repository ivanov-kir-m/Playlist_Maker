package com.practicum.playlistmaker.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.domain.player.model.Track
import com.practicum.playlistmaker.domain.playlists.model.Playlist
import com.practicum.playlistmaker.ui.player.adapter.PlaylistLinerAdapter
import com.practicum.playlistmaker.ui.player.view_model.PlayerViewModel
import com.practicum.playlistmaker.ui.search.SearchFragment.Companion.TRACK
import com.practicum.playlistmaker.utils.DateUtils
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlayerViewModel by viewModel()

    private var rvPlaylist: RecyclerView? = null
    private val adapterPlaylist = PlaylistLinerAdapter { clickOnPlaylist(it) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        @Suppress("DEPRECATION")
        val track = arguments?.getSerializable(TRACK) as Track
        viewModel.prepareTrack(track)
        super.onCreate(savedInstanceState)

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetPlayer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }
                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // binding.overlay.alpha = slideOffset
            }
        })

        binding.btnAddToPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        viewModel.searchViewState.observe(this) { state ->
            binding.btnPlay.isEnabled = state.playButtonEnabled
            binding.btnPlay.setImageResource(state.playButtonImage)
            binding.tvPlayTime.text = state.playTextTime
            binding.btnFavorites.setImageResource(
                if (state.favoriteBtn) {
                    R.drawable.icn_is_favorites
                } else {
                    R.drawable.icn_favourites
                }
            )
            adapterPlaylist.playlists = state.playlists
            adapterPlaylist.notifyDataSetChanged()
            if (state.playlistPanelHide) bottomSheetBehavior.state =
                BottomSheetBehavior.STATE_HIDDEN

        }

        binding.btnFavorites.setOnClickListener {
            viewModel.favoriteBtnFunction()
        }
        binding.btnPlay.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.tvTrackName.text = track.trackName

        binding.tvArtistName.text = track.artistName

        binding.tvTrackTime.text = DateUtils.millisToStrFormat(track.trackTimeMillis)

        Glide.with(binding.ivArtwork)
            .load(DateUtils.previewUrlSizeChange(track.artworkUrl100))
            .placeholder(R.drawable.default_art)
            .transform(RoundedCorners(binding.ivArtwork.resources.getDimensionPixelSize(R.dimen.art_work_radius_player)))
            .into(binding.ivArtwork)

        binding.tvCollectionName.apply {
            this.text = track.collectionName
            this.isVisible = track.collectionName.isNotEmpty()
        }

        binding.tvReleaseDate.text = DateUtils.strDateFormat(track.releaseDate)

        binding.tvPrimaryGenre.text = track.primaryGenreName

        binding.tvCountry.text = track.country

        binding.toolbarId.apply {
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }

        rvPlaylist = binding.rvPlaylist
        rvPlaylist!!.layoutManager = LinearLayoutManager(requireContext())
        rvPlaylist!!.adapter = adapterPlaylist

        viewModel.getPlaylists()

        binding.newPlaylistBtn.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_newPlaylistFragment)
        }
    }

    private fun clickOnPlaylist(playlist: Playlist) {
        viewModel.addIdTrackToPlaylist(playlist)
        viewModel.getPlaylists()
        when (viewModel.searchViewState.value?.thereTrackInPlaylist) {
            true -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.track_is_already_in_playlist, playlist.name),
                    Toast.LENGTH_SHORT
                ).show()
            }
            false -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.add_track_to_playlist, playlist.name),
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {}
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        rvPlaylist = null
    }
}