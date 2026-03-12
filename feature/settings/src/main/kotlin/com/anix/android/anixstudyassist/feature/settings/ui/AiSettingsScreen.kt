package com.anix.android.anixstudyassist.feature.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiSettingsScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Settings", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6200EE))
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item { AiSettingsSectionHeader("AI MODEL") }
            item {
                AiSettingsItem(
                    icon = Icons.Default.Psychology,
                    title = "AI Model",
                    subtitle = "Choose the AI model",
                    value = "GPT-4"
                )
            }
            item {
                AiSettingsItem(
                    icon = Icons.Default.FlashOn,
                    title = "Response Speed",
                    subtitle = "Balance between speed and quality",
                    value = "Balanced"
                )
            }
            item { AiSettingsSectionHeader("LANGUAGE & INPUT") }
            item {
                AiSettingsItem(
                    icon = Icons.Default.Translate,
                    title = "Language",
                    subtitle = "Preferred language for responses",
                    value = "English"
                )
            }
            item {
                AiSettingsItem(
                    icon = Icons.Default.Mic,
                    title = "Voice Input",
                    subtitle = "Enable voice to text",
                    value = ""
                )
            }
            item {
                Spacer(modifier = Modifier.height(32.dp))
                ClearHistoryButton()
            }
        }
    }
}

@Composable
fun AiSettingsSectionHeader(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray
    )
}

@Composable
fun AiSettingsItem(icon: ImageVector, title: String, subtitle: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color(0xFF6200EE),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(subtitle, color = Color.Gray, fontSize = 12.sp)
        }
        if (value.isNotEmpty()) {
            Text(
                value,
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.LightGray
        )
    }
}

@Composable
fun ClearHistoryButton() {
    OutlinedButton(
        onClick = { },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFB00020)),
        border = ButtonDefaults.outlinedButtonBorder(enabled = true)
            .copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFEEEEEE)))
    ) {
        Text("Clear All Chat History", fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
fun AiSettingsScreenPreview() {
    AiSettingsScreen(onBackClick = {})
}
