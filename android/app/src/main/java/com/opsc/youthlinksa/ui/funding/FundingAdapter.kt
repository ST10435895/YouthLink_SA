package com.opsc.youthlinksa.ui.funding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.opsc.youthlinksa.R
import com.opsc.youthlinksa.data.model.Funding

class FundingAdapter(
    private var items: List<Funding>,
    private val onClick: (Funding) -> Unit
) : RecyclerView.Adapter<FundingAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvProvider: TextView = view.findViewById(R.id.tvProvider)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val tvClosingDate: TextView = view.findViewById(R.id.tvClosingDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_funding, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvTitle.text = item.funding_title
        holder.tvProvider.text = item.provider ?: ""
        holder.tvAmount.text = item.amount?.let { "Amount: $it" } ?: ""
        holder.tvClosingDate.text = item.closing_date?.let { "Closing: $it" } ?: ""
        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<Funding>) {
        items = newItems
        notifyDataSetChanged()
    }
}
