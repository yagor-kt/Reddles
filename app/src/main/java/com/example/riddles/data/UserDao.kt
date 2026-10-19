package com.example.riddles.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): User?

    @Insert
    suspend fun insert(user: User): Long

    @Update
    suspend fun update(user: User)
}