package com.practicum.playlistmaker.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.activity.SearchActivity.Companion.TRACK
import com.practicum.playlistmaker.models.Track
import com.practicum.playlistmaker.utils.DateUtils
import java.text.SimpleDateFormat
import java.util.*

class PlayerActivity : AppCompatActivity() {

    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var trackTime: TextView
    private lateinit var artwork: ImageView
    private lateinit var collectionName: TextView
    private lateinit var collectionTitle: TextView
    private lateinit var releaseDate: TextView
    private lateinit var primaryGenreName: TextView
    private lateinit var country: TextView
    private lateinit var track: Track
    private lateinit var playTimeText: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_id)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        track = Gson().fromJson(intent.getStringExtra(TRACK), Track::class.java)

        trackName = findViewById<TextView>(R.id.trackNameText)
        trackName.text = track.trackName

        artistName = findViewById<TextView>(R.id.artistNameText)
        artistName.text = track.artistName

        trackTime = findViewById<TextView>(R.id.trackTime)
        trackTime.text = DateUtils.millisToStrFormat(track.trackTimeMillis)

        artwork = findViewById<ImageView>(R.id.artwork)
        Glide.with(artwork)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.default_art)
            .transform(RoundedCorners(artwork.resources.getDimensionPixelSize(R.dimen.art_work_radius_player)))
            .into(artwork)

        collectionName = findViewById<TextView>(R.id.collectionName)

        collectionTitle = findViewById<TextView>(R.id.collectionNameTitle)
        if(track.collectionName.isEmpty()){
            collectionName.visibility = View.GONE
            collectionTitle.visibility = View.GONE
        } else {
            collectionName.text = track.collectionName
        }

        releaseDate = findViewById<TextView>(R.id.releaseDate)
        val formatDate = SimpleDateFormat("yyyy", Locale.getDefault()).parse(track.releaseDate)
        val data = SimpleDateFormat("yyyy", Locale.getDefault()).format(formatDate)
        releaseDate.text =  data

        primaryGenreName = findViewById<TextView>(R.id.primaryGenre)
        primaryGenreName.text = track.primaryGenreName

        country = findViewById<TextView>(R.id.country)
        country.text = track.country

        playTimeText = findViewById(R.id.play_time)
    }
}
