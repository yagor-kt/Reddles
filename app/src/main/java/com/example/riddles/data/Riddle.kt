package com.example.riddles.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "riddles")
data class Riddle(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val level: String,
    val question: String,
    val option1: String,
    val option2: String,
    val option3: String,
    val option4: String,
    val correctIndex: Int
)