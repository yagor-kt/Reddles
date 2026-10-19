package com.example.riddles.ui.screens.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    private val _isLevelDialogVisible = MutableStateFlow(false)
    val isLevelDialogVisible = _isLevelDialogVisible.asStateFlow()

    fun showLevelDialog() {
        _isLevelDialogVisible.value = true
    }

    fun hideLevelDialog() {
        _isLevelDialogVisible.value = false
    }
}