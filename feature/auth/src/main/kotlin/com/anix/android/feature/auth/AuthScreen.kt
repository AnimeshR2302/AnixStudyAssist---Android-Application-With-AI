package com.anix.android.anixstudyassist.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.anix.android.anixstudyassist.core.nav.AuthScreenNavigations

@Composable
fun AuthScreen(
    navigations: AuthScreenNavigations
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Auth Screen")
        Button(onClick = { navigations.onLoginSuccess("demo_user") }) {
            Text("Login")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
    AuthScreen(object : AuthScreenNavigations {
        override fun onLoginSuccess(userId: String) {}
    })
}
