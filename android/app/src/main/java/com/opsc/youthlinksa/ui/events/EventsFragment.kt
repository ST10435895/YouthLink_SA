package com.opsc.youthlinksa.ui.events

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.opsc.youthlinksa.R
import com.opsc.youthlinksa.util.UiState

// "Events & Youth Programmes" feature: career exhibitions, job fairs,
// open days, workshops, and entrepreneurship events.
class EventsFragment : Fragment(R.layout.fragment_events) {

    private val viewModel: EventViewModel by viewModels()
    private lateinit var adapter: EventAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val tvEmpty = view.findViewById<TextView>(R.id.tvEmpty)

        adapter = EventAdapter(emptyList()) { event ->
            AlertDialog.Builder(requireContext())
                .setTitle(event.event_title)
                .setMessage(
                    buildString {
                        append(listOfNotNull(event.event_type, event.location).joinToString(" · "))
                        append("\n\n")
                        append(event.description ?: "No description available.")
                        event.start_date?.let { append("\n\nStarts: $it") }
                        event.end_date?.let { append("\nEnds: $it") }
                    }
                )
                .setPositiveButton("Close", null)
                .show()
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        swipeRefresh.setOnRefreshListener { viewModel.load() }

        viewModel.events.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    tvEmpty.visibility = View.GONE
                }
                is UiState.Success -> {
                    progressBar.visibility = View.GONE
                    swipeRefresh.isRefreshing = false
                    adapter.updateItems(state.data)
                    tvEmpty.visibility = if (state.data.isEmpty()) View.VISIBLE else View.GONE
                }
                is UiState.Error -> {
                    progressBar.visibility = View.GONE
                    swipeRefresh.isRefreshing = false
                    tvEmpty.text = state.message
                    tvEmpty.visibility = View.VISIBLE
                }
            }
        }

        viewModel.load()
    }
}
