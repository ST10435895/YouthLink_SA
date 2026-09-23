package com.opsc.youthlinksa.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.opsc.youthlinksa.data.model.LoginRequest
import com.opsc.youthlinksa.data.model.LoginResponse
import com.opsc.youthlinksa.data.model.RegisterRequest
import com.opsc.youthlinksa.data.network.RetrofitClient
import com.opsc.youthlinksa.util.UiState
import com.opsc.youthlinksa.util.ValidationUtils
import kotlinx.coroutines.launch
import org.json.JSONObject

class AuthViewModel : ViewModel() {

    private val _registerState = MutableLiveData<UiState<String>>()
    val registerState: LiveData<UiState<String>> = _registerState

    private val _loginState = MutableLiveData<UiState<LoginResponse>>()
    val loginState: LiveData<UiState<LoginResponse>> = _loginState

    fun register(firstName: String, surname: String, email: String, password: String) {
        // Basic input validation so the app never crashes on empty/invalid input
        val validationError = ValidationUtils.validateRegistration(
            firstName, surname, email, password
        )
        if (validationError != null) {
            _registerState.value = UiState.Error(validationError)
            return
        }

        _registerState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.register(
                    RegisterRequest(firstName, surname, email, password)
                )
                if (response.isSuccessful) {
                    _registerState.value = UiState.Success("Registration successful. Please log in.")
                } else {
                    _registerState.value = UiState.Error(extractError(response.errorBody()?.string()))
                }
            } catch (e: Exception) {
                _registerState.value = UiState.Error("Network error: ${e.localizedMessage ?: "could not reach the server."}")
            }
        }
    }

    fun login(email: String, password: String) {
        val validationError = ValidationUtils.validateLogin(email, password)
        if (validationError != null) {
            _loginState.value = UiState.Error(validationError)
            return
        }

        _loginState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body() != null) {
                    _loginState.value = UiState.Success(response.body()!!)
                } else {
                    _loginState.value = UiState.Error(extractError(response.errorBody()?.string()))
                }
            } catch (e: Exception) {
                _loginState.value = UiState.Error("Network error: ${e.localizedMessage ?: "could not reach the server."}")
            }
        }
    }

    // The API returns errors as {"error": "message"} - this pulls that out safely
    private fun extractError(body: String?): String {
        if (body.isNullOrBlank()) return "Something went wrong. Please try again."
        return try {
            JSONObject(body).optString("error", "Something went wrong. Please try again.")
        } catch (e: Exception) {
            "Something went wrong. Please try again."
        }
    }
}
