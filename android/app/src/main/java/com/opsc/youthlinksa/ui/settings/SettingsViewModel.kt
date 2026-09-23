package com.opsc.youthlinksa.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.opsc.youthlinksa.data.model.UpdateProfileRequest
import com.opsc.youthlinksa.data.model.UserProfile
import com.opsc.youthlinksa.data.network.RetrofitClient
import com.opsc.youthlinksa.util.UiState
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    private val _profile = MutableLiveData<UiState<UserProfile>>()
    val profile: LiveData<UiState<UserProfile>> = _profile

    private val _updateResult = MutableLiveData<UiState<String>>()
    val updateResult: LiveData<UiState<String>> = _updateResult

    fun loadProfile() {
        _profile.value = UiState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getProfile()
                if (response.isSuccessful && response.body() != null) {
                    _profile.value = UiState.Success(response.body()!!)
                } else {
                    _profile.value = UiState.Error("Could not load your profile.")
                }
            } catch (e: Exception) {
                _profile.value = UiState.Error("Network error: ${e.localizedMessage ?: "could not reach the server."}")
            }
        }
    }

    fun updateSettings(language: String, location: String, notificationsEnabled: Boolean) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.updateProfile(
                    UpdateProfileRequest(
                        language = language,
                        location = location.ifBlank { null },
                        notification_preference = notificationsEnabled
                    )
                )
                if (response.isSuccessful) {
                    _updateResult.value = UiState.Success("Settings saved.")
                } else {
                    _updateResult.value = UiState.Error("Could not save settings.")
                }
            } catch (e: Exception) {
                _updateResult.value = UiState.Error("Network error: ${e.localizedMessage ?: "could not reach the server."}")
            }
        }
    }
}
