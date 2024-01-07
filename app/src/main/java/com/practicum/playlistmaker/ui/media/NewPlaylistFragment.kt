package com.practicum.playlistmaker.ui.media

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
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.practicum.playlistmaker.ui.media.view_model.NewPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NewPlaylistViewModel by viewModel()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        binding.etName.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrEmpty()) {
                binding.btnCreate.isEnabled = true
                viewModel.setNameTextChanged(true)
                viewModel.playlistIsAlready(text.toString())
            } else {
                binding.btnCreate.isEnabled = false
                viewModel.setNameTextChanged(false)
            }
        } // слущатель изменени названия для активации кнопки создания и меняет флаг изменения имени

        binding.etDescription.doOnTextChanged { text, _, _, _ ->
            viewModel.setDescriptionTextChanged(!text.isNullOrEmpty())
        } // слушатель меняет флаг изменнения описания

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
                viewModel.saveData(
                    requireContext(),
                    name = name,
                    description = binding.etDescription.text.toString(),
                )
                findNavController().popBackStack()
            }
        }
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

    companion object {

        fun newInstance() = NewPlaylistFragment()

    }
}