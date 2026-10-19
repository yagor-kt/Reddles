package com.example.riddles.data

sealed interface LoginResult {
    data class Success(val user: User) : LoginResult
    data object InvalidPassword : LoginResult
}

class UserRepository(
    private val userDao: UserDao
) {
    suspend fun loginOrRegister(name: String, password: String): LoginResult {
        val existingUser = userDao.getByName(name)

        if (existingUser != null) {
            return if (existingUser.password == password) {
                LoginResult.Success(existingUser)
            } else {
                LoginResult.InvalidPassword
            }
        }

        val newUser = User(name = name, password = password)
        val insertedId = userDao.insert(newUser)

        return LoginResult.Success(newUser.copy(id = insertedId))
    }

    suspend fun updateStats(user: User, score: Int, isWin: Boolean): User {
        val updatedUser = user.copy(
            bestScore = maxOf(user.bestScore, score),
            totalGames = user.totalGames + 1,
            wins = user.wins + if (isWin) 1 else 0,
            losses = user.losses + if (isWin) 0 else 1
        )
        userDao.update(updatedUser)
        return updatedUser
    }
}