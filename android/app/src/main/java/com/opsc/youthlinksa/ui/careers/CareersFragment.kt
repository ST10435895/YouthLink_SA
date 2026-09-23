package com.opsc.youthlinksa.ui.careers

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

// "Career Exploration" feature: lets a user (especially one who hasn't
// decided on a career yet) browse career info - possible jobs,
// qualifications needed, and typical experience level.
class CareersFragment : Fragment(R.layout.fragment_careers) {

    private val viewModel: CareerViewModel by viewModels()
    private lateinit var adapter: CareerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val tvEmpty = view.findViewById<TextView>(R.id.tvEmpty)

        adapter = CareerAdapter(emptyList()) { career ->
            AlertDialog.Builder(requireContext())
                .setTitle(career.career_title)
                .setMessage(
                    buildString {
                        append(career.field?.let { "Field: $it\n\n" } ?: "")
                        append(career.description ?: "No description available.")
                        career.education_required?.let { append("\n\nTypical education: $it") }
                        career.experience_required?.let { append("\nTypical experience: $it") }
                    }
                )
                .setPositiveButton("Close", null)
                .show()
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        swipeRefresh.setOnRefreshListener { viewModel.load() }

        viewModel.careers.observe(viewLifecycleOwner) { state ->
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
