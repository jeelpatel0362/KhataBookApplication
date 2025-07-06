package com.example.khatabookapplication.Entity

enum class TransactionType {
    CREDIT,
    DEBIT;

    fun displayName(): String {
        return when (this) {
            CREDIT -> "Credit"
            DEBIT -> "Debit"
        }
    }
}