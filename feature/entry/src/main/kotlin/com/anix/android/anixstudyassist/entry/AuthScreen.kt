package com.anix.android.anixstudyassist.entry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anix.android.anixstudyassist.core.nav.AuthScreenNavigations
import com.anix.android.anixstudyassist.entry.viewmodel.AuthUiState
import com.anix.android.anixstudyassist.entry.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun EntryScreen(
    navigations: AuthScreenNavigations,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(uiState.loginSuccessUserId) {
        uiState.loginSuccessUserId?.let { userId ->
            delay(750)
            navigations.onLoginSuccess(userId)
            viewModel.onLoginNavigationHandled()
        }
    }

    AuthContent(
        uiState = uiState,
        onLoginClick = viewModel::onLoginClicked
    )
}

@Composable
private fun AuthContent(
    uiState: AuthUiState,
    onLoginClick: (String) -> Unit
) {
    val userIdState = rememberTextFieldState()
    val currentInput = userIdState.text.toString()
    val isLoginEnabled = currentInput.isNotBlank() &&
            currentInput.none(Char::isWhitespace) &&
            !uiState.isLoading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Login")
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            modifier = Modifier.fillMaxWidth(),
            state = userIdState,
            label = { Text("Username") },
            lineLimits = TextFieldLineLimits.SingleLine
        )

        uiState.statusMessage?.let { message ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                color = if (uiState.isLoginSuccessful) {
                    Color(0xFF2E7D32)
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LoginButton(
            onClick = onLoginClick,
            inputState = userIdState,
            enabled = isLoginEnabled,
            isLoading = uiState.isLoading
        )
    }
}

@Composable
private fun LoginButton(
    onClick: (String) -> Unit,
    inputState: TextFieldState,
    enabled: Boolean,
    isLoading: Boolean
) {
    Button(
        onClick = { onClick(inputState.text.toString()) },
        enabled = enabled
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Text("Login")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
    var state by remember { mutableStateOf(AuthUiState()) }
    AuthContent(
        uiState = state,
        onLoginClick = {
            state = if (it == "anix") {
                state.copy(statusMessage = "Login successful", isLoginSuccessful = true)
            } else {
                state.copy(statusMessage = "Invalid credentials", isLoginSuccessful = false)
            }
        }
    )
}
