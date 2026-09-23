package com.opsc.youthlinksa.ui.opportunities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.opsc.youthlinksa.R
import com.opsc.youthlinksa.data.model.Opportunity

class OpportunityAdapter(
    private var items: List<Opportunity>,
    private val onClick: (Opportunity) -> Unit
) : RecyclerView.Adapter<OpportunityAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvOrganisation: TextView = view.findViewById(R.id.tvOrganisation)
        val tvMeta: TextView = view.findViewById(R.id.tvMeta)
        val tvClosingDate: TextView = view.findViewById(R.id.tvClosingDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_opportunity, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvTitle.text = item.opportunity_title
        holder.tvOrganisation.text = item.organisation ?: ""
        holder.tvMeta.text = listOfNotNull(item.opportunity_type, item.location).joinToString(" · ")
        holder.tvClosingDate.text = item.closing_date?.let { "Closing: $it" } ?: ""
        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<Opportunity>) {
        items = newItems
        notifyDataSetChanged()
    }
}
