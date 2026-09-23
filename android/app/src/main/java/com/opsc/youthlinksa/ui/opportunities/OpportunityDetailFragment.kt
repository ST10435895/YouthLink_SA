package com.opsc.youthlinksa.ui.opportunities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.button.MaterialButton
import com.opsc.youthlinksa.R
import com.opsc.youthlinksa.util.UiState

class OpportunityDetailFragment : Fragment(R.layout.fragment_opportunity_detail) {

    private val viewModel: OpportunitiesViewModel by viewModels()
    private var applicationLink: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val opportunityId = arguments?.getInt("opportunity_id") ?: -1
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvOrganisation = view.findViewById<TextView>(R.id.tvOrganisation)
        val tvMeta = view.findViewById<TextView>(R.id.tvMeta)
        val tvDescription = view.findViewById<TextView>(R.id.tvDescription)
        val tvRequirements = view.findViewById<TextView>(R.id.tvRequirements)
        val tvClosingDate = view.findViewById<TextView>(R.id.tvClosingDate)
        val btnApply = view.findViewById<MaterialButton>(R.id.btnApply)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSave)

        btnApply.setOnClickListener {
            applicationLink?.let { link ->
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Could not open the application link.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnSave.setOnClickListener {
            if (opportunityId != -1) viewModel.saveOpportunity(opportunityId)
        }

        viewModel.saveResult.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> Toast.makeText(requireContext(), state.data, Toast.LENGTH_SHORT).show()
                is UiState.Error -> Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                else -> {}
            }
        }

        viewModel.opportunityDetail.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> progressBar.visibility = View.VISIBLE
                is UiState.Success -> {
                    progressBar.visibility = View.GONE
                    val o = state.data
                    applicationLink = o.application_link
                    tvTitle.text = o.opportunity_title
                    tvOrganisation.text = o.organisation ?: ""
                    tvMeta.text = listOfNotNull(o.opportunity_type, o.location).joinToString(" · ")
                    tvDescription.text = o.description ?: "No description provided."
                    tvRequirements.text = listOfNotNull(
                        o.education_requirement?.let { "Education: $it" },
                        o.experience_requirement?.let { "Experience: $it" }
                    ).joinToString("\n").ifBlank { "No specific requirements listed." }
                    tvClosingDate.text = o.closing_date?.let { "Closing date: $it" } ?: ""
                }
                is UiState.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        if (opportunityId != -1) {
            viewModel.loadDetail(opportunityId)
        } else {
            Toast.makeText(requireContext(), "Opportunity not found.", Toast.LENGTH_SHORT).show()
        }
    }
}
