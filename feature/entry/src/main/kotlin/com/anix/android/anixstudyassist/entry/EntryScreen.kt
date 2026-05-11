package com.anix.android.anixstudyassist.entry

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anix.android.anixstudyassist.entry.ui.LoginDialog
import com.anix.android.anixstudyassist.entry.viewmodel.AuthUiState
import com.anix.android.anixstudyassist.entry.viewmodel.EntryViewModel
import com.anix.android.anixstudyassist.ui.navigation.AuthScreenNavigations
import kotlinx.coroutines.delay

private const val TAG = "ANIX_Entry"

@Composable
fun EntryScreen(
    navigations: AuthScreenNavigations,
    viewModel: EntryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.loginSuccessUserId, uiState.showSuccessContent) {
        val userId = uiState.loginSuccessUserId
        if (uiState.showSuccessContent && userId != null) {
            Log.d(TAG, "Login success. UserID: $userId. Starting 5s delay before navigation.")
            delay(5_000)
            Log.d(TAG, "5s delay finished. Calling onLoginSuccess for $userId.")
            navigations.onLoginSuccess(userId)
            viewModel.onLoginNavigationHandled()
        }
    }

    AuthScreen(
        uiState = uiState,
        onUsernameChanged = viewModel::onUsernameChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        onJoinClick = viewModel::onJoinClicked
    )
}

@Composable
fun AuthScreen(
    uiState: AuthUiState,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onJoinClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.showSuccessContent) {
            AuthSuccessContent(modifier = Modifier.fillMaxSize())
        }

        if (uiState.isDialogVisible) {
            LoginDialog(
                usernameState = uiState.username,
                passwordState = uiState.password,
                passwordErrorMessage = uiState.passwordErrorMessage,
                isJoinEnabled = uiState.isJoinEnabled,
                isLoading = uiState.isLoading,
                onUsernameChanged = onUsernameChanged,
                onPasswordChanged = onPasswordChanged,
                onJoinClick = onJoinClick
            )
        }
    }
}

@Composable
private fun AuthSuccessContent(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val imageBitmap = remember(context) {
        context.assets.open("donkayy.jpg").use(BitmapFactory::decodeStream)?.asImageBitmap()
    }

    Box(modifier = modifier) {
        imageBitmap?.let {
            Image(
                bitmap = it,
                contentDescription = "DonKayy",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Text(
            text = "DonKayy Says Hi",
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center
        )
    }
}
