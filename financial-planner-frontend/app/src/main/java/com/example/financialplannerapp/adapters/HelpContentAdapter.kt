package com.example.financialplannerapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.financialplannerapp.R
import com.example.financialplannerapp.models.roomdb.HelpContent
import com.google.android.material.card.MaterialCardView

class HelpContentAdapter(
    private var helpContentList: List<HelpContent> = emptyList()
) : RecyclerView.Adapter<HelpContentAdapter.HelpContentViewHolder>() {

    fun updateData(newHelpContentList: List<HelpContent>) {
        helpContentList = newHelpContentList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelpContentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_help_content, parent, false)
        return HelpContentViewHolder(view)
    }

    override fun onBindViewHolder(holder: HelpContentViewHolder, position: Int) {
        holder.bind(helpContentList[position])
    }

    override fun getItemCount(): Int = helpContentList.size

    class HelpContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: MaterialCardView = itemView.findViewById(R.id.helpContentCard)
        private val titleText: TextView = itemView.findViewById(R.id.titleText)
        private val contentText: TextView = itemView.findViewById(R.id.contentText)
        private val expandIndicator: TextView = itemView.findViewById(R.id.expandIndicator)

        fun bind(helpContent: HelpContent) {
            titleText.text = helpContent.title
            contentText.text = helpContent.content
            
            // Initially collapse the content
            contentText.visibility = View.GONE
            expandIndicator.text = "Tap untuk membaca panduan lengkap"
            
            // Set click listener to expand/collapse
            cardView.setOnClickListener {
                if (contentText.visibility == View.GONE) {
                    contentText.visibility = View.VISIBLE
                    expandIndicator.text = "Tap untuk menyembunyikan panduan"
                } else {
                    contentText.visibility = View.GONE
                    expandIndicator.text = "Tap untuk membaca panduan lengkap"
                }
            }
        }
    }
}