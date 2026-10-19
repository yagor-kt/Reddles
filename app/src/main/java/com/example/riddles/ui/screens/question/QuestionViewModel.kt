package com.example.riddles.ui.screens.question

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.riddles.data.Riddle
import com.example.riddles.data.RiddleRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class QuestionUiState(
    val currentRiddle: Riddle? = null,
    val shuffledOptions: List<String> = emptyList(),
    val score: Int = 0,
    val answered: Boolean = false,
    val selectedAnswer: String? = null,
    val correctAnswer: String? = null,
    val isLoading: Boolean = true,
    val isEmpty: Boolean = false
)

data class RoundResult(
    val isWin: Boolean,
    val score: Int,
    val correctAnswer: String? = null
)

class QuestionViewModel(
    private val level: String,
    private val repository: RiddleRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(QuestionUiState())
    val uiState = _uiState.asStateFlow()

    private val _results = MutableSharedFlow<RoundResult>()
    val results = _results.asSharedFlow()

    private var riddles: List<Riddle> = emptyList()
    private var currentIndex = 0
    private val pointsPerAnswer = when (level) {
        "EASY" -> 10
        "MEDIUM" -> 20
        else -> 30
    }

    init {
        viewModelScope.launch {
            riddles = repository.getByLevel(level).shuffled()

            if (riddles.isEmpty()) {
                _uiState.value = QuestionUiState(
                    isLoading = false,
                    isEmpty = true
                )
            } else {
                showCurrentRiddle()
            }
        }
    }

    fun selectAnswer(answer: String) {
        val state = _uiState.value
        if (state.answered || state.currentRiddle == null) return

        val correctAnswer = state.correctAnswer ?: return
        val isCorrect = answer == correctAnswer
        val newScore = state.score + if (isCorrect) pointsPerAnswer else 0

        _uiState.value = state.copy(
            score = newScore,
            answered = true,
            selectedAnswer = answer
        )

        viewModelScope.launch {
            if (isCorrect) {
                delay(1_000)
                currentIndex += 1

                if (currentIndex >= riddles.size) {
                    _results.emit(
                        RoundResult(
                            isWin = true,
                            score = _uiState.value.score
                        )
                    )
                } else {
                    showCurrentRiddle()
                }
            } else {
                delay(1_500)
                _results.emit(
                    RoundResult(
                        isWin = false,
                        score = _uiState.value.score,
                        correctAnswer = correctAnswer
                    )
                )
            }
        }
    }

    private fun showCurrentRiddle() {
        val riddle = riddles[currentIndex]
        val options = listOf(
            riddle.option1,
            riddle.option2,
            riddle.option3,
            riddle.option4
        )
        val correctAnswer = options.getOrNull(riddle.correctIndex).orEmpty()

        _uiState.value = _uiState.value.copy(
            currentRiddle = riddle,
            shuffledOptions = options.shuffled(),
            answered = false,
            selectedAnswer = null,
            correctAnswer = correctAnswer,
            isLoading = false,
            isEmpty = false
        )
    }
}