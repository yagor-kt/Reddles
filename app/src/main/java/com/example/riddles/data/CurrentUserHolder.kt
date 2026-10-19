package com.example.riddles.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow

object CurrentUserHolder {
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    fun set(user: User?) {
        _user.value = user
    }

    fun update(transform: (User) -> User) {
        _user.value = _user.value?.let(transform)
    }
}