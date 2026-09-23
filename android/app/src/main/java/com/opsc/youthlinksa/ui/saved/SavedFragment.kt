package com.opsc.youthlinksa.ui.saved

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.opsc.youthlinksa.R
import com.opsc.youthlinksa.ui.opportunities.OpportunityAdapter
import com.opsc.youthlinksa.util.UiState

// "Saved Opportunities" feature: lets a user come back to opportunities
// they bookmarked earlier via the Save button on the detail screen.
// Reuses the same OpportunityAdapter and OpportunityDetailFragment as the
// Home screen, since it's the exact same Opportunity data type.
class SavedFragment : Fragment(R.layout.fragment_saved) {

    private val viewModel: SavedViewModel by viewModels()
    private lateinit var adapter: OpportunityAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val tvEmpty = view.findViewById<TextView>(R.id.tvEmpty)

        adapter = OpportunityAdapter(emptyList()) { opportunity ->
            val bundle = Bundle().apply { putInt("opportunity_id", opportunity.opportunity_id) }
            findNavController().navigate(R.id.opportunityDetailFragment, bundle)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        swipeRefresh.setOnRefreshListener { viewModel.load() }

        viewModel.saved.observe(viewLifecycleOwner) { state ->
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

        // Reload every time the screen becomes visible again, so a newly
        // saved/unsaved opportunity is reflected without needing a manual
        // pull-to-refresh. (Handled in onResume() below, which also covers
        // the first time this screen is shown.)
    }

    override fun onResume() {
        super.onResume()
        viewModel.load()
    }
}
