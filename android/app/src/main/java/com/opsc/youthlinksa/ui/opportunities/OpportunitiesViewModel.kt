package com.opsc.youthlinksa.ui.opportunities

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.opsc.youthlinksa.data.model.Opportunity
import com.opsc.youthlinksa.data.network.RetrofitClient
import com.opsc.youthlinksa.util.UiState
import kotlinx.coroutines.launch

class OpportunitiesViewModel : ViewModel() {

    private val _opportunities = MutableLiveData<UiState<List<Opportunity>>>()
    val opportunities: LiveData<UiState<List<Opportunity>>> = _opportunities

    private val _opportunityDetail = MutableLiveData<UiState<Opportunity>>()
    val opportunityDetail: LiveData<UiState<Opportunity>> = _opportunityDetail

    private val _saveResult = MutableLiveData<UiState<String>>()
    val saveResult: LiveData<UiState<String>> = _saveResult

    fun search(keyword: String? = null, field: String? = null, location: String? = null, type: String? = null) {
        _opportunities.value = UiState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getOpportunities(
                    keyword?.ifBlank { null },
                    field?.ifBlank { null },
                    location?.ifBlank { null },
                    type?.ifBlank { null }
                )
                if (response.isSuccessful && response.body() != null) {
                    _opportunities.value = UiState.Success(response.body()!!)
                } else {
                    _opportunities.value = UiState.Error("Could not load opportunities.")
                }
            } catch (e: Exception) {
                _opportunities.value = UiState.Error("Network error: ${e.localizedMessage ?: "could not reach the server."}")
            }
        }
    }

    fun loadDetail(id: Int) {
        _opportunityDetail.value = UiState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getOpportunity(id)
                if (response.isSuccessful && response.body() != null) {
                    _opportunityDetail.value = UiState.Success(response.body()!!)
                } else {
                    _opportunityDetail.value = UiState.Error("Could not load this opportunity.")
                }
            } catch (e: Exception) {
                _opportunityDetail.value = UiState.Error("Network error: ${e.localizedMessage ?: "could not reach the server."}")
            }
        }
    }

    fun saveOpportunity(id: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.saveOpportunity(id)
                if (response.isSuccessful) {
                    _saveResult.value = UiState.Success("Saved to your opportunities.")
                } else {
                    _saveResult.value = UiState.Error("Could not save this opportunity.")
                }
            } catch (e: Exception) {
                _saveResult.value = UiState.Error("Network error: ${e.localizedMessage ?: "could not reach the server."}")
            }
        }
    }
}
