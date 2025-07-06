package com.example.khatabookapplication.Entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val amount: Double,
    val title: String,
    val description: String?,
    val type: TransactionType,
    val nextPaymentDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)