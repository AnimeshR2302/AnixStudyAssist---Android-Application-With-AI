package com.anix.android.anixstudyassist.ui.navigation

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {
    companion object {
        private const val TAG = "ANIX_SharedVM"
    }

    private val _currentUser = MutableStateFlow<String?>(null)
    val currentUser: StateFlow<String?> = _currentUser

    fun setCurrentUser(user: String) {
        Log.d(TAG, "setCurrentUser: $user")
        _currentUser.value = user
    }

    fun clearUser() {
        Log.d(TAG, "clearUser: Previous user was ${_currentUser.value}")
        _currentUser.value = null
    }
}
