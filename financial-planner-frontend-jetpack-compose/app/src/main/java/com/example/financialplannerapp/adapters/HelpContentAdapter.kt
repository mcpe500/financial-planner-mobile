package com.example.financialplannerapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.financialplannerapp.R
import com.example.financialplannerapp.models.roomdb.HelpContent

class HelpContentAdapter(private var helpContentList: List<HelpContent>) : RecyclerView.Adapter<HelpContentAdapter.HelpContentViewHolder>() {

    class HelpContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(android.R.id.text1)
        val contentText: TextView = itemView.findViewById(android.R.id.text2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelpContentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false)
        return HelpContentViewHolder(view)
    }

    override fun onBindViewHolder(holder: HelpContentViewHolder, position: Int) {
        val helpContent = helpContentList[position]
        holder.titleText.text = helpContent.title
        holder.contentText.text = helpContent.content
        
        // Toggle content visibility on title click
        holder.titleText.setOnClickListener {
            holder.contentText.visibility = if (holder.contentText.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int = helpContentList.size    fun updateData(newHelpContentList: List<HelpContent>) {
        helpContentList = newHelpContentList
        notifyDataSetChanged()
    }
}