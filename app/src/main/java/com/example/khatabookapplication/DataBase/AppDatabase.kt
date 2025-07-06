package com.example.khatabookapplication.DataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.khatabookapplication.DAO.TransactionDao
import com.example.khatabookapplication.DAO.UserDao
import com.example.khatabookapplication.Entity.Transaction
import com.example.khatabookapplication.Entity.User

@Database(
    entities = [User::class, Transaction::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "khata_book_db"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}