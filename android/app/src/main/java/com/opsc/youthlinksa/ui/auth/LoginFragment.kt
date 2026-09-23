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
import com.opsc.youthlinksa.YouthLinkApp
import com.opsc.youthlinksa.util.UiState

class LoginFragment : Fragment(R.layout.fragment_login) {

    private val viewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etEmail = view.findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = view.findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = view.findViewById<MaterialButton>(R.id.btnLogin)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val tvError = view.findViewById<TextView>(R.id.tvError)

        view.findViewById<View>(R.id.tvGoRegister).setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }

        btnLogin.setOnClickListener {
            tvError.visibility = View.GONE
            viewModel.login(etEmail.text.toString().trim(), etPassword.text.toString())
        }

        viewModel.loginState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    btnLogin.isEnabled = false
                }
                is UiState.Success -> {
                    progressBar.visibility = View.GONE
                    btnLogin.isEnabled = true
                    // Save the token so the user stays logged in, then go Home
                    val app = requireActivity().application as YouthLinkApp
                    app.tokenManager.saveToken(state.data.token)
                    app.tokenManager.saveUserId(state.data.user.user_id)
                    findNavController().navigate(R.id.action_login_to_home)
                }
                is UiState.Error -> {
                    progressBar.visibility = View.GONE
                    btnLogin.isEnabled = true
                    tvError.text = state.message
                    tvError.visibility = View.VISIBLE
                }
            }
        }
    }
}
