package com.opsc.youthlinksa.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.opsc.youthlinksa.R
import com.opsc.youthlinksa.util.UiState

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private val viewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etFirstName = view.findViewById<TextInputEditText>(R.id.etFirstName)
        val etSurname = view.findViewById<TextInputEditText>(R.id.etSurname)
        val etEmail = view.findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = view.findViewById<TextInputEditText>(R.id.etPassword)
        val btnRegister = view.findViewById<MaterialButton>(R.id.btnRegister)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val tvError = view.findViewById<TextView>(R.id.tvError)

        view.findViewById<View>(R.id.tvGoLogin).setOnClickListener {
            findNavController().navigate(R.id.action_register_to_login)
        }

        btnRegister.setOnClickListener {
            tvError.visibility = View.GONE
            viewModel.register(
                etFirstName.text.toString().trim(),
                etSurname.text.toString().trim(),
                etEmail.text.toString().trim(),
                etPassword.text.toString()
            )
        }

        viewModel.registerState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    btnRegister.isEnabled = false
                }
                is UiState.Success -> {
                    progressBar.visibility = View.GONE
                    btnRegister.isEnabled = true
                    // Registration succeeded - send the user to log in with their new account
                    findNavController().navigate(R.id.action_register_to_login)
                }
                is UiState.Error -> {
                    progressBar.visibility = View.GONE
                    btnRegister.isEnabled = true
                    tvError.text = state.message
                    tvError.visibility = View.VISIBLE
                }
            }
        }
    }
}
