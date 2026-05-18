package com.anix.android.anixstudyassist.feature.datastore.presentation.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ClearDataDialog(onCancel: () -> Unit, onAgree: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Confirm Clear All") },
        text = { Text("Are you sure you want to clear all the data?") },
        confirmButton = { TextButton(onClick = onAgree) { Text("Clear All") } },
        dismissButton = { TextButton(onClick = onCancel) { Text("Cancel") } }
    )
}