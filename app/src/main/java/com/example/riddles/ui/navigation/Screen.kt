package com.example.riddles.ui.navigation

import android.net.Uri

object Screen {
    const val Login = "login"
    const val Home = "home"
    const val Question = "question/{level}"
    const val Result = "result/{isWin}/{score}/{correctAnswer}"
    const val Stats = "stats"

    fun questionRoute(level: String): String = "question/$level"

    fun resultRoute(
        isWin: Boolean,
        score: Int,
        correctAnswer: String?
    ): String {
        val answer = Uri.encode(correctAnswer ?: "-")
        return "result/$isWin/$score/$answer"
    }
}