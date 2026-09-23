package com.opsc.youthlinksa.ui.settings

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.opsc.youthlinksa.R
import com.opsc.youthlinksa.YouthLinkApp
import com.opsc.youthlinksa.util.UiState

// Settings screen: lets the user change language, location and notification
// preference. This satisfies the "user must be able to change their settings"
// requirement for Part 2.
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val viewModel: SettingsViewModel by viewModels()
    private val languages = listOf("English", "Afrikaans", "isiXhosa")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val spinnerLanguage = view.findViewById<Spinner>(R.id.spinnerLanguage)
        val etLocation = view.findViewById<EditText>(R.id.etLocation)
        val switchNotifications = view.findViewById<Switch>(R.id.switchNotifications)
        val tvMessage = view.findViewById<TextView>(R.id.tvMessage)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSave)
        val btnLogout = view.findViewById<MaterialButton>(R.id.btnLogout)

        spinnerLanguage.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, languages)

        btnSave.setOnClickListener {
            viewModel.updateSettings(
                language = spinnerLanguage.selectedItem.toString(),
                location = etLocation.text.toString().trim(),
                notificationsEnabled = switchNotifications.isChecked
            )
        }

        btnLogout.setOnClickListener {
            val app = requireActivity().application as YouthLinkApp
            app.tokenManager.clear()
            findNavController().navigate(R.id.action_settings_to_welcome)
        }

        viewModel.profile.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> progressBar.visibility = View.VISIBLE
                is UiState.Success -> {
                    progressBar.visibility = View.GONE
                    val profile = state.data
                    val index = languages.indexOf(profile.language ?: "English").coerceAtLeast(0)
                    spinnerLanguage.setSelection(index)
                    etLocation.setText(profile.location ?: "")
                    switchNotifications.isChecked = profile.notification_preference ?: true
                }
                is UiState.Error -> {
                    progressBar.visibility = View.GONE
                    tvMessage.text = state.message
                    tvMessage.visibility = View.VISIBLE
                }
            }
        }

        viewModel.updateResult.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    tvMessage.text = state.data
                    tvMessage.visibility = View.VISIBLE
                }
                is UiState.Error -> {
                    tvMessage.text = state.message
                    tvMessage.visibility = View.VISIBLE
                }
                else -> {}
            }
        }

        viewModel.loadProfile()
    }
}
