package com.practicum.playlistmaker.ui.playlist_details

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.practicum.playlistmaker.domain.MINUTE_FORMAT
import com.practicum.playlistmaker.domain.PLAYLIST
import com.practicum.playlistmaker.domain.TYPE_SHARE_LINK
import com.practicum.playlistmaker.domain.player.model.Track
import com.practicum.playlistmaker.domain.playlists.model.Playlist
import com.practicum.playlistmaker.ui.playlist_details.state.PlaylistsDetailsState
import com.practicum.playlistmaker.ui.playlist_details.view_model.PlaylistDetailsViewModel
import com.practicum.playlistmaker.ui.search.SearchFragment
import com.practicum.playlistmaker.ui.search.adapter.TracksAdapter
import com.practicum.playlistmaker.utils.DateUtils.millisToStrFormat
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*


class PlaylistDetailsFragment : Fragment() {

    private var _binding: FragmentPlaylistDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistDetailsViewModel by viewModel()

    private var playlist: Playlist? = null

    private var adapter: TracksAdapter? = null

    private var bottomSheetMenuBehavior: BottomSheetBehavior<ConstraintLayout>? = null

    private var bottomSheetPlaylistBehavior: BottomSheetBehavior<ConstraintLayout>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        @Suppress("DEPRECATION")
        playlist = arguments?.getSerializable(PLAYLIST) as Playlist
    }

    override fun onResume() {
        super.onResume()
        playlist?.playlistId?.let { viewModel.showPlayList(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.icBack.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.stateLiveData.observe(viewLifecycleOwner) {
            showPlaylist(it)
        }

        adapter = TracksAdapter(
            { clickOnTrack(it) },
            { showDeleteTrackDialog(it) }
        )
        binding.rvPlaylist.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvPlaylist.adapter = adapter


        binding.ivShareIc.setOnClickListener {
            sharePlaylistOrNot()
        }

        playlist?.playlistId?.let { viewModel.showPlayList(it) }

        binding.bottomSheetTracksList.doOnPreDraw {
            var peekHeight =
                PreferenceManager.getDefaultSharedPreferences(this.context).getInt("PEEKHEIGHT", 0)
            if (peekHeight <= 0) {
                val openMenuLocation = IntArray(2)
                binding.ivShareIc.getLocationInWindow(openMenuLocation)
                peekHeight =
                    binding.root.height - openMenuLocation[1] - resources.getDimensionPixelSize(R.dimen.margin_playlist_detail_24)
                super.onViewCreated(view, savedInstanceState)
                @Suppress("DEPRECATION")
                with(PreferenceManager.getDefaultSharedPreferences(this.context).edit()) {
                    putInt("PEEKHEIGHT", peekHeight)
                    apply()
                }
            }
            bottomSheetPlaylistBehavior = BottomSheetBehavior.from(binding.bottomSheetTracksList)
            bottomSheetPlaylistBehavior?.setPeekHeight(
                peekHeight,
                false
            ) // = 0 //openMenuHeightFromBottom //(binding.root.height * 0.2).toInt()
            bottomSheetPlaylistBehavior?.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_EXPANDED -> {
                            binding.bottomSheetTracksList.setBackgroundResource(R.color.background_frag)
                        }

                        else -> {
                            binding.bottomSheetTracksList.setBackgroundResource(R.drawable.rounder_sheet)
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        }

        binding.bottomSheetTracksList.setOnClickListener {}

        bottomSheetMenuBehavior = BottomSheetBehavior.from(binding.bottomSheetMenu).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetMenuBehavior?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.isVisible = false
                    }

                    else -> {
                        binding.overlay.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.overlay.setOnClickListener {}

        binding.ivMenuIc.setOnClickListener {
            bottomSheetMenuBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.tvShareMenu.setOnClickListener {
            sharePlaylistOrNot()
        }

        binding.tvDeletePlaylistMenu.setOnClickListener {
            showDeletePlaylistDialog()
        }

        binding.tvEditInfoMenu.setOnClickListener {
            findNavController().navigate(
                R.id.action_playlistDetailsFragment_to_newPlaylistFragment,
                Bundle().apply {
                    putSerializable(PLAYLIST, playlist)
                }
            )

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        playlist = null
        adapter = null
        bottomSheetMenuBehavior = null
        bottomSheetPlaylistBehavior = null
    }

    private fun setNameAndDescription(state: PlaylistsDetailsState) {
        binding.tvNamePlaylist.text = state.name
        binding.tvPlaylistNameMenu.text = state.name

        if (state.description.isEmpty()) {
            binding.tvDescriptionPlaylist.isVisible = false
        } else {
            binding.tvDescriptionPlaylist.text = state.description
        }
        binding.tvCountTracks.text = requireContext().resources.getQuantityString(
            R.plurals.tracks_hint,
            state.countTracks,
            state.countTracks,
        )
        binding.tvCountTracksMenu.text = requireContext().resources.getQuantityString(
            R.plurals.tracks_hint,
            state.countTracks,
            state.countTracks,
        )
        binding.tvTimeTracks.text = minutesTracksInPlaylist(state.timeTracksMillis)
    }

    private fun setPicture(state: PlaylistsDetailsState) {
        if (
            state.picture != null &&
            state.picture.toString() != "null" &&
            !state.picture.equals(Uri.EMPTY)
        ) {
            binding.ivArtworkPlaylist.setImageURI(state.picture)
            binding.ivMenuArt.setImageURI(state.picture)
        } else {
            binding.ivArtworkPlaylist.setImageResource(R.drawable.default_playlist_art)
            binding.ivMenuArt.setImageResource(R.drawable.default_art)
        }
    }

    private fun setTracks(state: PlaylistsDetailsState) {
        if (state.tracksList.isEmpty()) {
            adapter?.tracks?.clear()
            adapter?.notifyDataSetChanged()
            Toast.makeText(
                requireContext(),
                R.string.no_track_in_playlist,
                Toast.LENGTH_SHORT
            )
                .show()
        } else {
            adapter?.tracks?.clear()
            adapter?.tracks?.addAll(state.tracksList)
            adapter?.invertList()
        }
    }

    private fun showPlaylist(state: PlaylistsDetailsState) {
        setNameAndDescription(state)
        setPicture(state)
        setTracks(state)

    }

    private fun minutesTracksInPlaylist(millis: Int): String {
        val minutes = SimpleDateFormat(MINUTE_FORMAT, Locale.getDefault()).format(millis).toInt()
        return resources.getQuantityString(R.plurals.minutes, minutes, minutes)
    }

    private fun clickOnTrack(track: Track) {
        findNavController().navigate(
            R.id.action_playlistDetailsFragment_to_playerFragment,
            Bundle().apply { putSerializable(SearchFragment.TRACK, track) }
        )
    }

    private fun showDeleteTrackDialog(track: Track) {
        val deleteDialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialog)
            .setTitle(requireContext().getString(R.string.delete_track))
            .setMessage(requireContext().getString(R.string.u_sure_delete_track))
            .setNegativeButton(R.string.cancel) { _, _ ->
                // ничего не делаем
            }.setPositiveButton(R.string.delete) { _, _ ->
                viewModel.deleteTrack(track)
            }
        deleteDialog.show()
    }

    private fun showDeletePlaylistDialog() {
        bottomSheetMenuBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
        val deleteDialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialog)
            .setTitle(requireContext().getString(R.string.delete_playlist))
            .setMessage(requireContext().getString(R.string.u_sure_delete_playlist))
            .setNegativeButton(R.string.cancel) { _, _ ->
                // ничего не делаем
            }.setPositiveButton(R.string.yes) { _, _ ->
                viewModel.deletePlaylist {
                    findNavController().popBackStack()
                }
            }
        deleteDialog.show()
    }

    private fun playlistIsEmpty(): Boolean {
        return adapter?.tracks.isNullOrEmpty()
    }

    private fun shareText(): String {
        var text = "${playlist?.name}\n${playlist?.description}\n${binding.tvCountTracks.text}"
        adapter?.tracks?.forEachIndexed { i, track ->
            text += "\n${i + 1}. ${track.artistName} - ${track.trackName} (${millisToStrFormat(track.trackTimeMillis)})"
        }
        return text
    }

    private fun sharePlaylist() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, shareText())
            type = TYPE_SHARE_LINK
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        requireContext().startActivity(intent)
    }

    private fun sharePlaylistOrNot() {
        if (playlistIsEmpty()) {
            Toast.makeText(requireContext(), R.string.empty_track_list, Toast.LENGTH_SHORT)
                .show()
        } else {
            sharePlaylist()
        }
        bottomSheetMenuBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
    }
}