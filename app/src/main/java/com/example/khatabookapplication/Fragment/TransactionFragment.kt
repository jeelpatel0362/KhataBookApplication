package com.example.khatabookapplication.Fragment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.khatabookapplication.Adapter.TransactionAdapter
import com.example.khatabookapplication.DataBase.AppDatabase
import com.example.khatabookapplication.Entity.Transaction
import com.example.khatabookapplication.Entity.TransactionType
import com.example.khatabookapplication.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.concurrent.TimeUnit

class TransactionFragment : Fragment() {

    private lateinit var appDatabase: AppDatabase
    private lateinit var rvTransactions: RecyclerView
    private lateinit var btnAddTransaction: Button
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var tvEmptyView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_transaction, container, false)
        rvTransactions = view.findViewById(R.id.rv_transactions)
        btnAddTransaction = view.findViewById(R.id.btn_add_transaction)
        tvEmptyView = view.findViewById(R.id.tv_empty_view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appDatabase = AppDatabase.getInstance(requireContext())

        setupRecyclerView()
        setupClickListeners()
        loadTransactions()
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(requireContext(), emptyList())
        rvTransactions.apply {
            adapter = transactionAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupClickListeners() {
        btnAddTransaction.setOnClickListener {
            showAddTransactionDialog()
        }
    }

    private fun loadTransactions() {
        lifecycleScope.launch {
            try {
                val transactions = withContext(Dispatchers.IO) {
                    appDatabase.transactionDao().getAllTransactions()
                }
                if (transactions.isEmpty()) {
                    tvEmptyView.visibility = View.VISIBLE
                    rvTransactions.visibility = View.GONE
                } else {
                    tvEmptyView.visibility = View.GONE
                    rvTransactions.visibility = View.VISIBLE
                    transactionAdapter.updateData(transactions)
                }
            } catch (e: Exception) {
                showToast("Error loading transactions: ${e.message}")
            }
        }
    }

    private fun showAddTransactionDialog() {
        val dialog = Dialog(requireContext()).apply {
            setContentView(R.layout.activity_add_transaction_dialog)
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setCancelable(true)
        }

        val spinnerUser = dialog.findViewById<Spinner>(R.id.spinner_user)
        val etAmount = dialog.findViewById<EditText>(R.id.et_amount)
        val etTitle = dialog.findViewById<EditText>(R.id.et_title)
        val etDescription = dialog.findViewById<EditText>(R.id.et_description)
        val rgType = dialog.findViewById<RadioGroup>(R.id.rg_transaction_type)
        val btnSave = dialog.findViewById<Button>(R.id.btn_save_transaction)

        loadUsersIntoSpinner(dialog, spinnerUser)

        btnSave.setOnClickListener {
            val amount = etAmount.text.toString().toDoubleOrNull()
            val title = etTitle.text.toString().trim()
            val description = etDescription.text.toString().trim().takeIf { it.isNotBlank() }
            val selectedUserPos = spinnerUser.selectedItemPosition
            val type = if (rgType.checkedRadioButtonId == R.id.rb_credit) {
                TransactionType.CREDIT
            } else {
                TransactionType.DEBIT
            }

            when {
                amount == null -> etAmount.error = "Enter valid amount"
                amount <= 0 -> etAmount.error = "Amount must be positive"
                title.isEmpty() -> etTitle.error = "Title is required"
                selectedUserPos == -1 -> showToast("Please select a user")
                else -> saveTransaction(dialog, amount, title, description, selectedUserPos, type)
            }
        }

        dialog.show()
    }

    private fun loadUsersIntoSpinner(dialog: Dialog, spinnerUser: Spinner) {
        lifecycleScope.launch {
            try {
                val users = withContext(Dispatchers.IO) {
                    appDatabase.userDao().getAllUsers()
                }
                if (users.isEmpty()) {
                    dialog.dismiss()
                    showToast("No users available. Please add users first.")
                    return@launch
                }

                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    users.map { it.name }
                ).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
                withContext(Dispatchers.Main) {
                    spinnerUser.adapter = adapter
                }
            } catch (e: Exception) {
                showToast("Error loading users: ${e.message}")
            }
        }
    }

    private fun saveTransaction(
        dialog: Dialog,
        amount: Double,
        title: String,
        description: String?,
        selectedUserPos: Int,
        type: TransactionType
    ) {
        lifecycleScope.launch {
            try {
                val users = withContext(Dispatchers.IO) {
                    appDatabase.userDao().getAllUsers()
                }
                val selectedUser = users.getOrNull(selectedUserPos) ?: run {
                    showToast("Invalid user selection")
                    return@launch
                }

                val nextPaymentDate = if (type == TransactionType.DEBIT) {
                    val nextDate = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7)
                    Log.d("Transaction", "Setting next payment date: ${Date(nextDate)}")
                    nextDate
                } else null

                val transaction = Transaction(
                    userId = selectedUser.id,
                    amount = amount,
                    title = title,
                    description = description,
                    type = type,
                    nextPaymentDate = nextPaymentDate
                )

                withContext(Dispatchers.IO) {
                    appDatabase.transactionDao().insertTransaction(transaction)
                    Log.d("Transaction", "Saved transaction: $transaction")
                }

                loadTransactions()
                dialog.dismiss()
                showToast("Transaction saved successfully")
            } catch (e: Exception) {
                Log.e("Transaction", "Error saving transaction", e)
                showToast("Error saving transaction: ${e.message}")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}