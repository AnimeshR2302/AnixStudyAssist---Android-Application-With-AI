package com.anix.android.anixstudyassist.feature.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anix.android.anixstudyassist.feature.auth.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onUserIdChanged(userId: String) {
        _uiState.update { it.copy(userId = userId, errorMessage = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun onLoginClicked() {
        val state = _uiState.value
        if (state.userId.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter user ID and password") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val isSuccess = loginUseCase(
                userId = state.userId.trim(),
                password = state.password
            )

            _uiState.update {
                if (isSuccess) {
                    it.copy(
                        isLoading = false,
                        errorMessage = null,
                        loginSuccessUserId = state.userId.trim()
                    )
                } else {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Invalid credentials"
                    )
                }
            }
        }
    }

    fun onLoginNavigationHandled() {
        _uiState.update { it.copy(loginSuccessUserId = null) }
    }
}

data class AuthUiState(
    val userId: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loginSuccessUserId: String? = null
)
