package com.anix.android.anixstudyassist.feature.datastore.presentation.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anix.android.anixstudyassist.feature.datastore.presentation.viewmodel.DataStoreViewModel
import java.util.Locale
import kotlin.math.log10
import kotlin.math.pow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataStoreScreen(
    onBackClick: () -> Unit,
    viewModel: DataStoreViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.addData(it) }
    }

    if (uiState.showClearConfirmDialog) {
        ClearDataDialog(viewModel::removeClearAllDialog, viewModel::onConfirmDeleteAll)
    }

    Scaffold(
        containerColor = Color(0xFFF3F4F6),
        topBar = {
            TopAppBar(
                title = { Text("Data Store", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::showClearAllConfirmDialog) {
                        Icon(
                            Icons.Default.DeleteSweep,
                            contentDescription = "Clear All",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6200EE))
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.files.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Store is empty", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.files) { fileName ->
                        Text(
                            text = fileName,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { filePickerLauncher.launch("*/*") },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(horizontal = 32.dp)
                ) {
                    Text("+ Add Data")
                }
                Text(
                    text = "Total Size: ${formatSize(uiState.totalSize)}",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

private fun formatSize(size: Long): String {
    if (size <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
    return String.format(
        Locale.getDefault(),
        "%.1f %s",
        size / 1024.0.pow(digitGroups.toDouble()),
        units[digitGroups]
    )
}
