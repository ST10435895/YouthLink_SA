package com.opsc.youthlinksa.ui.saved

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.opsc.youthlinksa.data.model.Opportunity
import com.opsc.youthlinksa.data.network.RetrofitClient
import com.opsc.youthlinksa.util.UiState
import kotlinx.coroutines.launch

// Handles loading the current user's saved/bookmarked opportunities from
// GET /api/opportunities/saved/me.
class SavedViewModel : ViewModel() {

    private val _saved = MutableLiveData<UiState<List<Opportunity>>>()
    val saved: LiveData<UiState<List<Opportunity>>> = _saved

    fun load() {
        _saved.value = UiState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getSavedOpportunities()
                if (response.isSuccessful && response.body() != null) {
                    _saved.value = UiState.Success(response.body()!!)
                } else {
                    _saved.value = UiState.Error("Could not load your saved opportunities.")
                }
            } catch (e: Exception) {
                _saved.value = UiState.Error(
                    "Network error: ${e.localizedMessage ?: "could not reach the server."}"
                )
            }
        }
    }
}
