package com.example.khatabookapplication.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.khatabookapplication.Entity.User

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("SELECT * FROM users ORDER BY name ASC")
    suspend fun getAllUsers(): List<User>
}