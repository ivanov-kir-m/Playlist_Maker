package com.practicum.playlistmaker.ui.new_playlist

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.practicum.playlistmaker.domain.PLAYLIST
import com.practicum.playlistmaker.domain.playlists.model.Playlist
import com.practicum.playlistmaker.ui.new_playlist.view_model.NewPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NewPlaylistViewModel by viewModel()
    private var playlist: Playlist? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        @Suppress("DEPRECATION")
        playlist = arguments?.getSerializable(PLAYLIST) as Playlist?
    }

    private fun initPlaylist() {
        playlist?.let {
            binding.toolbarId.title = getString(R.string.editor)
            binding.btnCreate.text = getString(R.string.save)
            binding.btnCreate.isEnabled = true
            if (
                playlist?.picture != null &&
                playlist?.picture.toString() != "null" &&
                !playlist?.picture?.equals(Uri.EMPTY)!!
            ) {
                binding.ivArtwork.setImageURI(Uri.parse(playlist!!.picture!!))
            }
            binding.etName.setText(playlist?.name)
            binding.etDescription.setText(playlist?.description)
        }
    }

    private fun initListeners() {
        binding.etName.doOnTextChanged { text, _, _, _ ->
            binding.btnCreate.isEnabled = !text.isNullOrEmpty()
            viewModel.setNameTextChanged(textChanged(text))
            viewModel.playlistIsAlready(text.toString())
        } // слущатель изменени названия для активации кнопки создания и меняет флаг изменения имени

        binding.etDescription.doOnTextChanged { text, _, _, _ ->
            viewModel.setDescriptionTextChanged(textChanged(text))
        } // слушатель меняет флаг изменнения описания

        //Переопределил нажатие на кнопку назад
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showOrNotClosingDialog()
                }
            }
        )

        binding.toolbarId.setNavigationOnClickListener {
            showOrNotClosingDialog()
        } //бинд кнопки назад на тулбаре

        //регистрируем событие, которое вызывает photo picker
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                //обрабатываем событие выбора пользователем фотографии
                viewModel.setPictureUri(
                    if (uri != null) {
                        binding.ivArtwork.setImageURI(uri)
                        uri
                    } else {
                        null
                    }
                )
            }

        binding.ivArtwork.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } // запуск выбора картинки по нажатию на ImageView

        binding.btnCreate.setOnClickListener {
            val name = binding.etName.text.toString()
            viewModel.playlistIsAlready(name)
            if (viewModel.newPlaylistViewState.value?.playlistAlready == true) {
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.playlist_is_already, name),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                viewModel.createNewPlaylist(
                    name = name,
                    description = binding.etDescription.text.toString(),
                )
                findNavController().popBackStack()
            }
        }

        binding.btnCreate.setOnClickListener {
            if (playlist == null) {
                addNewPlaylist()
            } else {
                updatePlaylist()
            }
        }
    }

    private fun setObserver() {
        viewModel.newPlaylistViewState.observe(this, Observer {
            it.playlistCreatedEvent?.getContentIfNotHandled()
                ?.let { // Only proceed if the event has never been handled
                    Toast.makeText(
                        requireContext(),
                        requireContext().getString(R.string.add_new_playlist_massage, it),
                        Toast.LENGTH_SHORT
                    ).show()
                }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        initListeners()
        initPlaylist()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showOrNotClosingDialog() {
        val closingDialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialog)
            .setTitle(requireContext().getString(R.string.finish_create_playlist))
            .setMessage(requireContext().getString(R.string.unsaved_data_lost))
            .setNegativeButton(R.string.cancel) { _, _ ->
                // ничего не делаем
            }.setPositiveButton(R.string.finish) { _, _ ->
                findNavController().popBackStack() // переход назад
            }
        playlist?.let {
            closingDialog.setTitle(R.string.finish_update_playlist)
        }
        if (dataIsFilled()) {
            closingDialog.show()
        } else {
            findNavController().popBackStack()
        }
    }

    private fun dataIsFilled(): Boolean {
        return (
                viewModel.newPlaylistViewState.value?.pictureUri != null ||
                        viewModel.newPlaylistViewState.value?.nameTextChanged == true ||
                        viewModel.newPlaylistViewState.value?.descriptionTextChanged == true
                )
    }

    private fun updatePlaylist() {
        playlist?.let {
            viewModel.updatePlaylist(
                id = it.playlistId,
                newName = binding.etName.text.toString(),
                newDescription = binding.etDescription.text.toString(),
            ) {
                findNavController().popBackStack()
            }
        }
    }

    private fun addNewPlaylist() {
        val name = binding.etName.text.toString()
        viewModel.playlistIsAlready(name)
        if (viewModel.newPlaylistViewState.value?.playlistAlready == true) {
            Toast.makeText(
                requireContext(),
                requireContext().getString(R.string.playlist_is_already, name),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            viewModel.createNewPlaylist(
                name = name,
                description = binding.etDescription.text.toString(),
            )
            Toast.makeText(
                requireContext(),
                requireContext().getString(R.string.add_new_playlist_massage, name),
                Toast.LENGTH_SHORT
            ).show()
        }
        findNavController().popBackStack()
    }

    private fun textChanged(text: CharSequence?): Boolean {
        if (playlist == null) {
            return !text.isNullOrEmpty()
        } else {
            return playlist!!.description != text.toString()
        }
    }

    companion object {
        fun newInstance() = NewPlaylistFragment()
    }
}