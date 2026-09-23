package com.opsc.youthlinksa.ui.careers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.opsc.youthlinksa.data.model.Career
import com.opsc.youthlinksa.data.network.RetrofitClient
import com.opsc.youthlinksa.util.UiState
import kotlinx.coroutines.launch

// Handles loading career-exploration content from GET /api/careers.
// This is the "Career Exploration" feature from the Part 1 design doc,
// aimed at users (e.g. high school learners) who haven't decided on a
// career path yet.
class CareerViewModel : ViewModel() {

    private val _careers = MutableLiveData<UiState<List<Career>>>()
    val careers: LiveData<UiState<List<Career>>> = _careers

    fun load(field: String? = null) {
        _careers.value = UiState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getCareers(field?.ifBlank { null })
                if (response.isSuccessful && response.body() != null) {
                    _careers.value = UiState.Success(response.body()!!)
                } else {
                    _careers.value = UiState.Error("Could not load careers.")
                }
            } catch (e: Exception) {
                _careers.value = UiState.Error(
                    "Network error: ${e.localizedMessage ?: "could not reach the server."}"
                )
            }
        }
    }
}
