package com.example.riddles.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface RiddleDao {
    @Query("SELECT * FROM riddles WHERE level = :level")
    suspend fun getByLevel(level: String): List<Riddle>
}