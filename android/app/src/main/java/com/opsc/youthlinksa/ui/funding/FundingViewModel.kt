package com.opsc.youthlinksa.ui.funding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.opsc.youthlinksa.data.model.Funding
import com.opsc.youthlinksa.data.network.RetrofitClient
import com.opsc.youthlinksa.util.UiState
import kotlinx.coroutines.launch

// Handles loading funding/bursary content from GET /api/funding.
// This is the "Study & Funding" feature from the Part 1 design doc.
class FundingViewModel : ViewModel() {

    private val _funding = MutableLiveData<UiState<List<Funding>>>()
    val funding: LiveData<UiState<List<Funding>>> = _funding

    fun load() {
        _funding.value = UiState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getFunding()
                if (response.isSuccessful && response.body() != null) {
                    _funding.value = UiState.Success(response.body()!!)
                } else {
                    _funding.value = UiState.Error("Could not load funding opportunities.")
                }
            } catch (e: Exception) {
                _funding.value = UiState.Error(
                    "Network error: ${e.localizedMessage ?: "could not reach the server."}"
                )
            }
        }
    }
}
