package com.example.financialplannerapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.financialplannerapp.R
import com.example.financialplannerapp.models.roomdb.FAQItem

class FAQAdapter(private var faqList: List<FAQItem>) : RecyclerView.Adapter<FAQAdapter.FAQViewHolder>() {

    class FAQViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val questionText: TextView = itemView.findViewById(android.R.id.text1)
        val answerText: TextView = itemView.findViewById(android.R.id.text2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false)
        return FAQViewHolder(view)
    }

    override fun onBindViewHolder(holder: FAQViewHolder, position: Int) {
        val faq = faqList[position]
        holder.questionText.text = faq.question
        holder.answerText.text = faq.answer
        
        // Toggle answer visibility on question click
        holder.questionText.setOnClickListener {
            holder.answerText.visibility = if (holder.answerText.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
    }    override fun getItemCount(): Int = faqList.size

    fun updateData(newFaqList: List<FAQItem>) {
        faqList = newFaqList
        notifyDataSetChanged()
    }
}