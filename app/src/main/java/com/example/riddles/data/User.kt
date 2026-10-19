package com.example.riddles.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["name"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val password: String,
    val bestScore: Int = 0,
    val totalGames: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0
)