package com.anix.android.anixstudyassist.feature.landing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.anix.android.anixstudyassist.core.nav.LandingScreenNavigations

@Composable
fun LandingScreen(
    navigations: LandingScreenNavigations,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { navigations.onOpenClass("class-101") }) {
            Text("Navigate to Class Details")
        }
        Button(onClick = navigations.onOpenSettings) {
            Text("Navigate to Settings")
        }
        Button(onClick = navigations.onLogout) {
            Text("Logout")
        }
    }
}
