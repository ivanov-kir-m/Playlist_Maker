package com.practicum.playlistmaker.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.models.Track

class TracksViewHolder(parentView: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(
        parentView.context
    ).inflate(
        R.layout.track_item_view, parentView,
        false
    )
) {

    private val trackName: TextView = itemView.findViewById(R.id.trackNameText)
    private val artistName: TextView = itemView.findViewById(R.id.artistNameText)
    private val trackTime: TextView = itemView.findViewById(R.id.trackTimeText)
    private val artwork: ImageView = itemView.findViewById(R.id.artwork)

    fun bind(model: Track) {
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = model.trackTime
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.default_art)
            .transform(
                RoundedCorners(
                    itemView.resources.getDimensionPixelSize(
                        R.dimen.default_art_radius
                    )
                )
            )
            .into(artwork)
    }
}