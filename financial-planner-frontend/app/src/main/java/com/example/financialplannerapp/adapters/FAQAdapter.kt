package com.example.financialplannerapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.financialplannerapp.R
import com.example.financialplannerapp.models.roomdb.FAQItem
import com.google.android.material.card.MaterialCardView

class FAQAdapter(
    private var faqList: List<FAQItem> = emptyList()
) : RecyclerView.Adapter<FAQAdapter.FAQViewHolder>() {

    fun updateData(newFaqList: List<FAQItem>) {
        faqList = newFaqList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_faq, parent, false)
        return FAQViewHolder(view)
    }

    override fun onBindViewHolder(holder: FAQViewHolder, position: Int) {
        holder.bind(faqList[position])
    }

    override fun getItemCount(): Int = faqList.size

    class FAQViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: MaterialCardView = itemView.findViewById(R.id.faqCard)
        private val questionText: TextView = itemView.findViewById(R.id.questionText)
        private val answerText: TextView = itemView.findViewById(R.id.answerText)
        private val popularIndicator: TextView = itemView.findViewById(R.id.popularIndicator)
        private val expandIndicator: TextView = itemView.findViewById(R.id.expandIndicator)

        fun bind(faqItem: FAQItem) {
            questionText.text = faqItem.question
            answerText.text = faqItem.answer
            
            // Show/hide popular indicator
            popularIndicator.visibility = if (faqItem.isPopular) View.VISIBLE else View.GONE
            
            // Initially collapse the answer
            answerText.visibility = View.GONE
            expandIndicator.text = "Tap untuk melihat jawaban"
            
            // Set click listener to expand/collapse
            cardView.setOnClickListener {
                if (answerText.visibility == View.GONE) {
                    answerText.visibility = View.VISIBLE
                    expandIndicator.text = "Tap untuk menyembunyikan jawaban"
                } else {
                    answerText.visibility = View.GONE
                    expandIndicator.text = "Tap untuk melihat jawaban"
                }
            }
        }
    }
}