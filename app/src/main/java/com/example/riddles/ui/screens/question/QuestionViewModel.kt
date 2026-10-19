package com.example.riddles.ui.screens.question

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.riddles.data.Riddle
import com.example.riddles.data.RiddleRepository
import kotlinx.coroutines.Job
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
    val attemptsLeft: Int = 3,
    val timeLeft: Int = 15,
    val questionNumber: Int = 0,
    val totalQuestions: Int = 0,
    val timedOut: Boolean = false,
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
    private var timerJob: Job? = null

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
        submitAnswer(answer = answer, timedOut = false)
    }

    private fun submitAnswer(answer: String?, timedOut: Boolean) {
        val state = _uiState.value
        if (state.answered || state.currentRiddle == null) return

        val correctAnswer = state.correctAnswer ?: return
        val isCorrect = !timedOut && answer == correctAnswer
        val attemptsLeft = state.attemptsLeft - if (isCorrect) 0 else 1

        timerJob?.cancel()

        _uiState.value = state.copy(
            score = state.score + if (isCorrect) pointsPerAnswer else 0,
            answered = true,
            selectedAnswer = answer,
            attemptsLeft = attemptsLeft,
            timeLeft = if (timedOut) 0 else state.timeLeft,
            timedOut = timedOut
        )

        viewModelScope.launch {
            if (isCorrect) {
                delay(FEEDBACK_DELAY_MS)
                advanceToNextRiddle()
            } else {
                delay(WRONG_ANSWER_DELAY_MS)

                if (attemptsLeft <= 0) {
                    _results.emit(
                        RoundResult(
                            isWin = false,
                            score = _uiState.value.score,
                            correctAnswer = correctAnswer
                        )
                    )
                } else {
                    advanceToNextRiddle()
                }
            }
        }
    }

    private fun advanceToNextRiddle() {
        currentIndex += 1

        if (currentIndex >= riddles.size) {
            viewModelScope.launch {
                _results.emit(
                    RoundResult(
                        isWin = true,
                        score = _uiState.value.score
                    )
                )
            }
        } else {
            showCurrentRiddle()
        }
    }

    private fun showCurrentRiddle() {
        timerJob?.cancel()

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
            timeLeft = QUESTION_TIME_SECONDS,
            questionNumber = currentIndex + 1,
            totalQuestions = riddles.size,
            timedOut = false,
            isLoading = false,
            isEmpty = false
        )

        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.timeLeft > 0 && !_uiState.value.answered) {
                delay(1_000)

                val state = _uiState.value
                if (state.answered) return@launch

                val updatedTime = (state.timeLeft - 1).coerceAtLeast(0)
                _uiState.value = state.copy(timeLeft = updatedTime)

                if (updatedTime == 0) {
                    submitAnswer(answer = null, timedOut = true)
                    return@launch
                }
            }
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }

    private companion object {
        const val QUESTION_TIME_SECONDS = 15
        const val FEEDBACK_DELAY_MS = 1_500L
        const val WRONG_ANSWER_DELAY_MS = 3_000L
    }
}