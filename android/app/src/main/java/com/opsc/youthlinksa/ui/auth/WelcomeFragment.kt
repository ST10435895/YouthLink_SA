package com.opsc.youthlinksa.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.opsc.youthlinksa.R

class WelcomeFragment : Fragment(R.layout.fragment_welcome) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.btnLogin).setOnClickListener {
            findNavController().navigate(R.id.action_welcome_to_login)
        }
        view.findViewById<View>(R.id.btnRegister).setOnClickListener {
            findNavController().navigate(R.id.action_welcome_to_register)
        }
    }
}
