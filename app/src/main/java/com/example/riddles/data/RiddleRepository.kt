package com.example.riddles.data

class RiddleRepository(
    private val riddleDao: RiddleDao
) {
    suspend fun getByLevel(level: String): List<Riddle> {
        return riddleDao.getByLevel(level)
    }
}