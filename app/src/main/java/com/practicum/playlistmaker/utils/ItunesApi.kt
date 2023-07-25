package com.practicum.playlistmaker.utils

import com.practicum.playlistmaker.models.TrackResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface ItunesApi {
    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<TrackResponse>
}