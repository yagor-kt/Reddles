package com.example.riddles.ui.screens.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.riddles.data.CurrentUserHolder
import com.example.riddles.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ResultUiState(
    val previousBestScore: Int = 0,
    val isNewRecord: Boolean = false,
    val isUpdating: Boolean = false
)

class ResultViewModel(
    private val userRepository: UserRepository,
    private val score: Int,
    private val isWin: Boolean
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResultUiState())
    val uiState = _uiState.asStateFlow()

    private var updateStarted = false

    fun recordResult() {
        if (updateStarted) return
        updateStarted = true

        viewModelScope.launch {
            val user = CurrentUserHolder.user.value
            if (user == null) {
                _uiState.value = ResultUiState()
                return@launch
            }

            val previousBest = user.bestScore
            _uiState.value = ResultUiState(
                previousBestScore = previousBest,
                isNewRecord = isWin && score > previousBest,
                isUpdating = true
            )

            val updatedUser = userRepository.updateStats(
                user = user,
                score = score,
                isWin = isWin
            )
            CurrentUserHolder.set(updatedUser)

            _uiState.value = ResultUiState(
                previousBestScore = previousBest,
                isNewRecord = isWin && score > previousBest,
                isUpdating = false
            )
        }
    }
}