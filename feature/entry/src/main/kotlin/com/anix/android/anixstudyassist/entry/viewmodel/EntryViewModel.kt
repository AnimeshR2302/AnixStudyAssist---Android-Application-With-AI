package com.anix.android.anixstudyassist.entry.viewmodel

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anix.android.anixstudyassist.entry.domain.model.AuthSuccessData
import com.anix.android.anixstudyassist.entry.domain.model.LoginResult
import com.anix.android.anixstudyassist.entry.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EntryViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onUsernameChanged(text: String) {
        val state = _uiState.value
        state.username.setTextAndPlaceCursorAtEnd(text)
        clearPasswordError()
        syncDerivedState()
    }

    fun onPasswordChanged(text: String) {
        val state = _uiState.value
        state.password.setTextAndPlaceCursorAtEnd(text)
        clearPasswordError()
        syncDerivedState()
    }

    fun onJoinClicked() {
        val currentState = _uiState.value
        if (!currentState.isJoinEnabled) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    authResult = UiResult.Loading,
                    passwordErrorMessage = null,
                    isJoinEnabled = false
                )
            }

            when (val result = loginUseCase(currentState.username.text.toString(), currentState.password.text.toString())) {
                is LoginResult.Success -> {
                    _uiState.update {
                        it.copy(
                            authResult = UiResult.Success(AuthSuccessData(userId = result.userId)),
                            passwordErrorMessage = null,
                            isDialogVisible = false,
                            showSuccessContent = true,
                            loginSuccessUserId = result.userId,
                            isJoinEnabled = false
                        )
                    }
                }

                LoginResult.InvalidCredentials -> {
                    _uiState.update {
                        it.copy(
                            authResult = UiResult.Error("Invalid credentials"),
                            passwordErrorMessage = "Invalid credentials",
                            isDialogVisible = true,
                            showSuccessContent = false,
                            loginSuccessUserId = null,
                            isJoinEnabled = it.password.text.isNotEmpty()
                        )
                    }
                }

                is LoginResult.NetworkError -> {
                    _uiState.update {
                        it.copy(
                            authResult = UiResult.Error(result.message),
                            passwordErrorMessage = result.message,
                            isDialogVisible = true,
                            showSuccessContent = false,
                            loginSuccessUserId = null,
                            isJoinEnabled = it.password.text.isNotEmpty()
                        )
                    }
                }
            }
        }
    }

    fun onLoginNavigationHandled() {
        _uiState.update {
            it.copy(
                loginSuccessUserId = null,
                showSuccessContent = false
            )
        }
    }

    private fun clearPasswordError() {
        _uiState.update { state ->
            if (state.passwordErrorMessage == null && state.authResult !is UiResult.Error) {
                state
            } else {
                state.copy(
                    passwordErrorMessage = null,
                    authResult = UiResult.Idle
                )
            }
        }
    }

    private fun syncDerivedState() {
        _uiState.update { state ->
            state.copy(
                isJoinEnabled = state.password.text.isNotEmpty() && state.authResult !is UiResult.Loading
            )
        }
    }
}

data class AuthUiState(
    val username: TextFieldState = TextFieldState(),
    val password: TextFieldState = TextFieldState(),
    val authResult: UiResult<AuthSuccessData> = UiResult.Idle,
    val passwordErrorMessage: String? = null,
    val isJoinEnabled: Boolean = false,
    val isDialogVisible: Boolean = true,
    val showSuccessContent: Boolean = false,
    val loginSuccessUserId: String? = null
) {
    val isLoading: Boolean
        get() = authResult is UiResult.Loading
}
