package com.practicum.playlistmaker.di


import com.practicum.playlistmaker.ui.favorites.view_model.FavoritesViewModel
import com.practicum.playlistmaker.ui.new_playlist.view_model.NewPlaylistViewModel
import com.practicum.playlistmaker.ui.player.view_model.PlayerViewModel
import com.practicum.playlistmaker.ui.playlist_details.view_model.PlaylistDetailsViewModel
import com.practicum.playlistmaker.ui.playlists.view_model.PlaylistsViewModel
import com.practicum.playlistmaker.ui.search.view_model.SearchViewModel
import com.practicum.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        PlayerViewModel(get(), get())
    }

    viewModel {
        SearchViewModel(get())
    }

    viewModel {
        FavoritesViewModel(get())
    }

    viewModel {
        PlaylistsViewModel(get())
    }

    viewModel {
        NewPlaylistViewModel(get())
    }

    viewModel {
        PlaylistDetailsViewModel(get())
    }

}