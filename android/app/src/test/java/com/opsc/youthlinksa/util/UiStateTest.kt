package com.opsc.youthlinksa.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

// UiState is what every screen in the app uses to represent
// loading/success/error - these tests confirm each variant carries the
// right data, since a mistake here would affect every screen at once.
class UiStateTest {

    @Test
    fun `Loading is a singleton with no data`() {
        val state: UiState<String> = UiState.Loading
        assertTrue(state is UiState.Loading)
    }

    @Test
    fun `Success carries the given data`() {
        val state: UiState<List<Int>> = UiState.Success(listOf(1, 2, 3))
        assertTrue(state is UiState.Success)
        assertEquals(listOf(1, 2, 3), (state as UiState.Success).data)
    }

    @Test
    fun `Error carries the given message`() {
        val state: UiState<String> = UiState.Error("Network error")
        assertTrue(state is UiState.Error)
        assertEquals("Network error", (state as UiState.Error).message)
    }
}
