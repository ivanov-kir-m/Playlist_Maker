package com.practicum.playlistmaker.ui.media.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.playlists.model.Playlist


class PlaylistsViewHolder(parentView: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater
        .from(parentView.context)
        .inflate(R.layout.playlist_item_view, parentView, false)
) {
    private val ivArtwork: ImageView = itemView.findViewById(R.id.iv_artwork_item)
    private val tvName: TextView = itemView.findViewById(R.id.tv_name_playlist)
    private val tvCountTrack: TextView = itemView.findViewById(R.id.tv_count_track)

    fun bind(model: Playlist) {
        tvName.text = model.name
        tvCountTrack.text = tvCountTrack.resources.getQuantityString(
            R.plurals.tracks_hint,
            model.numbersOfTrack,
            model.numbersOfTrack,
        )

        if (
            model.picture != null &&
            model.picture.toString() != "null" &&
            !model.picture.equals(Uri.EMPTY)
        ) {
            ivArtwork.setImageURI(model.picture)
        } else {
            ivArtwork.setImageResource(R.drawable.default_art)
        }
    }
}