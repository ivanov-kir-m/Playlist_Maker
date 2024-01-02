package com.practicum.playlistmaker.ui.player.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.domain.player.model.Track
import com.practicum.playlistmaker.ui.player.view_model.PlayerViewModel
import com.practicum.playlistmaker.ui.search.SearchFragment.Companion.TRACK
import com.practicum.playlistmaker.utils.DateUtils.millisToStrFormat
import com.practicum.playlistmaker.utils.DateUtils.previewUrlSizeChange
import com.practicum.playlistmaker.utils.DateUtils.strDateFormat
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private val viewModel: PlayerViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        @Suppress("DEPRECATION") val track = intent.getSerializableExtra(TRACK) as Track
        viewModel.prepareTrack(track)
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.searchViewState.observe(this) { state ->
            binding.playBtn.isEnabled = state.playButtonEnabled
            binding.playBtn.setImageResource(state.playButtonImage)
            binding.playTime.text = state.playTextTime
            binding.favouritesBtn.setImageResource(
                if (state.favoriteBtn) {
                    R.drawable.icn_is_favorites
                } else {
                    R.drawable.icn_favourites
                }
            )
        }
        binding.playBtn.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.favouritesBtn.setOnClickListener {
            viewModel.favoriteButtonFunction()
        }

        binding.artistNameText.text = track.artistName

        binding.trackTime.text = millisToStrFormat(track.trackTimeMillis)

        Glide.with(binding.artwork)
            .load(previewUrlSizeChange(track.artworkUrl100))
            .placeholder(R.drawable.default_art)
            .transform(RoundedCorners(binding.artwork.resources.getDimensionPixelSize(R.dimen.art_work_radius_player)))
            .into(binding.artwork)

        binding.collectionName.apply {
            this.text = track.collectionName
            this.isVisible = track.collectionName.isNotEmpty()
        }

        binding.releaseDate.text = strDateFormat(track.releaseDate)

        binding.primaryGenre.text = track.primaryGenreName

        binding.country.text = track.country

        binding.toolbarId.apply {
            setNavigationOnClickListener {
                finish()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }
}
