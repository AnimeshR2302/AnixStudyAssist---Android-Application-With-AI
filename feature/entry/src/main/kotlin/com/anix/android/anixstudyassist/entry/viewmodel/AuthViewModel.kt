package com.anix.android.anixstudyassist.entry.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anix.android.anixstudyassist.entry.domain.usecase.LoginUseCase
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

    companion object {
        private const val DEV_PASSWORD = "anix"
    }

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onLoginClicked(userId: String) {
        val sanitizedUserId = userId.trim()
        if (sanitizedUserId.isBlank() || userId.any(Char::isWhitespace)) {
            _uiState.update {
                it.copy(
                    isLoginSuccessful = false,
                    statusMessage = "Enter a username without spaces"
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    isLoginSuccessful = false,
                    statusMessage = null
                )
            }

            val isSuccess = loginUseCase(
                userId = sanitizedUserId,
                password = DEV_PASSWORD
            )

            _uiState.update {
                if (isSuccess) {
                    it.copy(
                        isLoading = false,
                        isLoginSuccessful = true,
                        statusMessage = "Login successful",
                        loginSuccessUserId = sanitizedUserId
                    )
                } else {
                    it.copy(
                        isLoading = false,
                        isLoginSuccessful = false,
                        statusMessage = "Invalid credentials"
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
    val isLoading: Boolean = false,
    val statusMessage: String? = null,
    val isLoginSuccessful: Boolean = false,
    val loginSuccessUserId: String? = null
)
