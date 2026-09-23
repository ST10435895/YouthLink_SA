package com.opsc.youthlinksa.ui.opportunities

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.opsc.youthlinksa.R
import com.opsc.youthlinksa.util.UiState

// Home screen: shows the opportunity search/filter feature (search box +
// field/type/location filters + results list). This is the "Opportunity
// Search & Filters" feature from the Part 1 design document.
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: OpportunitiesViewModel by viewModels()
    private lateinit var adapter: OpportunityAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etSearch = view.findViewById<TextInputEditText>(R.id.etSearch)
        val spinnerField = view.findViewById<AutoCompleteTextView>(R.id.spinnerField)
        val spinnerType = view.findViewById<AutoCompleteTextView>(R.id.spinnerType)
        val etLocation = view.findViewById<TextInputEditText>(R.id.etLocation)
        val btnApplyFilters = view.findViewById<MaterialButton>(R.id.btnApplyFilters)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val tvEmpty = view.findViewById<TextView>(R.id.tvEmpty)

        adapter = OpportunityAdapter(emptyList()) { opportunity ->
            val bundle = Bundle().apply { putInt("opportunity_id", opportunity.opportunity_id) }
            findNavController().navigate(R.id.action_home_to_opportunityDetail, bundle)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        view.findViewById<ImageButton>(R.id.btnSettings).setOnClickListener {
            findNavController().navigate(R.id.action_home_to_settings)
        }

        // Runs a search using whatever is currently in the search box and
        // the three filter fields. "All Fields" / "All Types" and blank
        // location are treated as "no filter" by the ViewModel/API.
        fun runSearch() {
            viewModel.search(
                keyword = etSearch.text.toString(),
                field = spinnerField.text.toString().takeIf { it != getString(R.string.filter_all_fields) },
                location = etLocation.text.toString(),
                type = spinnerType.text.toString().takeIf { it != getString(R.string.filter_all_types) }
            )
        }

        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                runSearch()
                true
            } else false
        }

        btnApplyFilters.setOnClickListener { runSearch() }

        swipeRefresh.setOnRefreshListener { runSearch() }

        viewModel.opportunities.observe(viewLifecycleOwner) { state ->
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

        // Load everything on first open
        viewModel.search()
    }
}
