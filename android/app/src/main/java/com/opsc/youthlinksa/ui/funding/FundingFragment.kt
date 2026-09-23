package com.opsc.youthlinksa.ui.funding

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.opsc.youthlinksa.R
import com.opsc.youthlinksa.util.UiState

// "Study & Funding" feature: bursaries, NSFAS-type funding, and other
// financial support young people can apply for.
class FundingFragment : Fragment(R.layout.fragment_funding) {

    private val viewModel: FundingViewModel by viewModels()
    private lateinit var adapter: FundingAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val tvEmpty = view.findViewById<TextView>(R.id.tvEmpty)

        adapter = FundingAdapter(emptyList()) { funding ->
            val dialogBuilder = AlertDialog.Builder(requireContext())
                .setTitle(funding.funding_title)
                .setMessage(
                    buildString {
                        append(funding.provider?.let { "Provider: $it\n\n" } ?: "")
                        append(funding.description ?: "No description available.")
                        funding.amount?.let { append("\n\nAmount: $it") }
                        funding.eligibility?.let { append("\n\nEligibility: $it") }
                        funding.closing_date?.let { append("\n\nClosing date: $it") }
                    }
                )
                .setNegativeButton("Close", null)

            if (!funding.application_link.isNullOrBlank()) {
                dialogBuilder.setPositiveButton("Apply") { _, _ ->
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(funding.application_link)))
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Could not open the application link.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            dialogBuilder.show()
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        swipeRefresh.setOnRefreshListener { viewModel.load() }

        viewModel.funding.observe(viewLifecycleOwner) { state ->
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
