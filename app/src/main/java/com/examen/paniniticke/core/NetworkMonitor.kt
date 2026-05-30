package com.examen.paniniticke.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NetworkMonitor {

    private val _isOnline = MutableStateFlow(true)

    val isOnline: Flow<Boolean> = _isOnline.asStateFlow()

    fun simulateOffline() {
        _isOnline.value = false
    }

    fun simulateOnline() {
        _isOnline.value = true
    }
}
