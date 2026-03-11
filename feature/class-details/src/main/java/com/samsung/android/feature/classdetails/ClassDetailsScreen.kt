package com.anix.android.anixstudyassist.feature.classdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.anix.android.anixstudyassist.core.nav.ClassDetailsScreenNavigations

@Composable
fun ClassDetailsScreen(
    classId: String,
    navigations: ClassDetailsScreenNavigations
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Class Details Screen: $classId")
        Button(onClick = navigations.onBack) {
            Text("Back")
        }
        Button(onClick = { navigations.onOpenSettings(classId) }) {
            Text("Open Settings")
        }
        Button(onClick = navigations.onLogout) {
            Text("Logout")
        }
    }
}
