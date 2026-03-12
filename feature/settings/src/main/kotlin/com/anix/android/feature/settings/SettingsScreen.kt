package com.anix.android.anixstudyassist.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.anix.android.anixstudyassist.core.nav.SettingsScreenNavigations

@Composable
fun SettingsScreen(
    navigations: SettingsScreenNavigations
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Settings Screen")
        Button(onClick = navigations.onBack) {
            Text("Back")
        }
        Button(onClick = { navigations.onOpenSetting("setting_1") }) {
            Text("Open Setting 1")
        }
        Button(onClick = navigations.onLogout) {
            Text("Logout")
        }
    }
}
