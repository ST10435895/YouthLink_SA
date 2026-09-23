package com.opsc.youthlinksa.ui.careers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.opsc.youthlinksa.R
import com.opsc.youthlinksa.data.model.Career

class CareerAdapter(
    private var items: List<Career>,
    private val onClick: (Career) -> Unit
) : RecyclerView.Adapter<CareerAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvField: TextView = view.findViewById(R.id.tvField)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_career, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvTitle.text = item.career_title
        holder.tvField.text = item.field ?: ""
        holder.tvDescription.text = item.description ?: ""
        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<Career>) {
        items = newItems
        notifyDataSetChanged()
    }
}
