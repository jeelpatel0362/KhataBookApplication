package com.example.khatabookapplication.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.khatabookapplication.Entity.Transaction
import com.example.khatabookapplication.Entity.User

@Dao
interface TransactionDao {
    @Insert
    suspend fun insertTransaction(transaction: Transaction): Long

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("SELECT * FROM transactions ORDER BY createdAt DESC")
    suspend fun getAllTransactions(): List<Transaction>

    @Query(
        """
        SELECT * FROM transactions 
        WHERE date(createdAt/1000, 'unixepoch') = date('now') 
        ORDER BY createdAt DESC
    """
    )
    suspend fun getTodayTransactions(): List<Transaction>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'CREDIT'")
    suspend fun getTotalCredit(): Double

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'DEBIT'")
    suspend fun getTotalDebit(): Double

    @Query("""
    SELECT DISTINCT u.* FROM users u
    JOIN transactions t ON u.id = t.userId
    WHERE t.type = 'DEBIT' 
    AND t.nextPaymentDate IS NOT NULL
    AND datetime(t.nextPaymentDate/1000, 'unixepoch') <= datetime('now', 'localtime')
    AND NOT EXISTS (
        SELECT 1 FROM transactions t2 
        WHERE t2.userId = t.userId 
        AND t2.type = 'CREDIT' 
        AND t2.description LIKE '%payment received%'
        AND date(t2.createdAt/1000, 'unixepoch', 'localtime') = date('now', 'localtime')
    )
""")
    suspend fun getUsersWithPendingPayments(): List<User>
}