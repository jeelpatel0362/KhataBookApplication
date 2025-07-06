package com.example.khatabookapplication.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.khatabookapplication.Entity.Transaction
import com.example.khatabookapplication.Entity.TransactionType
import com.example.khatabookapplication.R

class TransactionAdapter(private val context: Context, private var transactions: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tv_title)
        val tvAmount: TextView = view.findViewById(R.id.tv_amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.tvTitle.text = transaction.title
        holder.tvAmount.text = "₹${transaction.amount}"
        holder.tvAmount.setTextColor(ContextCompat.getColor(context,
            if (transaction.type == TransactionType.CREDIT) R.color.green else R.color.red))
    }

    override fun getItemCount() = transactions.size

    fun updateData(newTransactions: List<Transaction>) {
        this.transactions = newTransactions
        notifyDataSetChanged()
    }
}