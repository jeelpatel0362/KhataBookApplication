package com.example.khatabookapplication.Fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.khatabookapplication.DataBase.AppDatabase
import com.example.khatabookapplication.Entity.Transaction
import com.example.khatabookapplication.Entity.TransactionType
import com.example.khatabookapplication.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {
    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AppDatabase.getInstance(requireContext())
        loadSummaryData(view)
    }

    private fun loadSummaryData(view: View) {
        lifecycleScope.launch {
            try {
                val (totalCredit, totalDebit) = withContext(Dispatchers.IO) {
                    Pair(
                        db.transactionDao().getTotalCredit(),
                        db.transactionDao().getTotalDebit()
                    )
                }

                view.findViewById<TextView>(R.id.tv_total_credit).text =
                    "Total Credit: $totalCredit"
                view.findViewById<TextView>(R.id.tv_total_debit).text =
                    "Total Debit: $totalDebit"

                val todayTransactions = withContext(Dispatchers.IO) {
                    db.transactionDao().getTodayTransactions()
                }
                displayTransactions(view, todayTransactions)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun displayTransactions(view: View, transactions: List<Transaction>) {
        val transactionsContainer = view.findViewById<LinearLayout>(R.id.ll_transactions)
        transactionsContainer.removeAllViews()

        if (transactions.isEmpty()) {
            val emptyView = TextView(requireContext()).apply {
                text = "No transactions today"
                gravity = View.TEXT_ALIGNMENT_CENTER
                setTextAppearance(android.R.style.TextAppearance_Medium)
            }
            transactionsContainer.addView(emptyView)
            return
        }

        transactions.forEach { transaction ->
            val transactionView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_transaction, transactionsContainer, false)

            transactionView.findViewById<TextView>(R.id.tv_title).text = transaction.title
            transactionView.findViewById<TextView>(R.id.tv_amount).text =
                "Amount: ${transaction.amount}"
            transactionView.findViewById<TextView>(R.id.tv_type).text =
                "Type: ${transaction.type}"

            transactionView.setBackgroundColor(
                if (transaction.type == TransactionType.CREDIT) {
                    Color.parseColor("#E8F5E9")
                } else {
                    Color.parseColor("#FFEBEE")
                }
            )

            transactionsContainer.addView(transactionView)
        }
    }
}