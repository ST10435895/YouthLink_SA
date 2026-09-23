package com.opsc.youthlinksa.ui.events

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.opsc.youthlinksa.data.model.Event
import com.opsc.youthlinksa.data.network.RetrofitClient
import com.opsc.youthlinksa.util.UiState
import kotlinx.coroutines.launch

// Handles loading youth events/programmes from GET /api/events.
// This is the "Events & Youth Programmes" feature from the Part 1 design doc
// (career exhibitions, job fairs, open days, workshops, etc.).
class EventViewModel : ViewModel() {

    private val _events = MutableLiveData<UiState<List<Event>>>()
    val events: LiveData<UiState<List<Event>>> = _events

    fun load() {
        _events.value = UiState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getEvents()
                if (response.isSuccessful && response.body() != null) {
                    _events.value = UiState.Success(response.body()!!)
                } else {
                    _events.value = UiState.Error("Could not load events.")
                }
            } catch (e: Exception) {
                _events.value = UiState.Error(
                    "Network error: ${e.localizedMessage ?: "could not reach the server."}"
                )
            }
        }
    }
}
