package com.opsc.youthlinksa.ui.events

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.opsc.youthlinksa.R
import com.opsc.youthlinksa.data.model.Event

class EventAdapter(
    private var items: List<Event>,
    private val onClick: (Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvMeta: TextView = view.findViewById(R.id.tvMeta)
        val tvDates: TextView = view.findViewById(R.id.tvDates)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvTitle.text = item.event_title
        holder.tvMeta.text = listOfNotNull(item.event_type, item.location).joinToString(" · ")
        holder.tvDates.text = listOfNotNull(
            item.start_date?.let { "Starts: $it" },
            item.end_date?.let { "Ends: $it" }
        ).joinToString("   ")
        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<Event>) {
        items = newItems
        notifyDataSetChanged()
    }
}
