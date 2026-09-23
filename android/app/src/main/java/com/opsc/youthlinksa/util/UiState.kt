package com.opsc.youthlinksa.util

// A simple wrapper so ViewModels can expose "loading / success / error"
// states to fragments in one LiveData, instead of three separate ones.
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
