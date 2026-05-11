package com.anix.android.anixstudyassist.feature.settings.presentation.ui

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anix.android.anixstudyassist.ui.theme.AnixColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onSettingClick: (String) -> Unit = {}
) {
    Scaffold(
        containerColor = Color(0xFFF3F4F6),
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = Color.White, fontWeight = FontWeight.Bold) },
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
            item { UserProfileHeader() }
            item { SettingsSectionHeader("ACCOUNT") }
            item {
                SettingsItem(
                    Icons.Default.Person,
                    Color.Blue,
                    "Profile",
                    "Manage your profile information",
                    onClick = { onSettingClick("profile") }
                )
            }
            item {
                SettingsItem(
                    Icons.Default.Notifications,
                    Color.Yellow,
                    "Notifications",
                    "Configure notification preferences",
                    onClick = { onSettingClick("notifications") }
                )
            }
            item { SettingsSectionHeader("PREFERENCES") }
            item {
                SettingsItem(
                    Icons.Default.Palette,
                    Color.Cyan,
                    "Appearance",
                    "Theme and display settings",
                    onClick = { onSettingClick("appearance") }
                )
            }
            item { SettingsSectionHeader("SUPPORT") }
            item {
                SettingsItem(
                    Icons.Default.Info,
                    Color.DarkGray,
                    "About",
                    "App version and information",
                    onClick = { onSettingClick("about") }
                )
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
                LogoutButton(onClick = onLogoutClick)
            }
        }
    }
}

@Composable
fun UserProfileHeader() {
    val colors = AnixColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(60.dp),
            shape = CircleShape,
            color = Color(0xFFE8EAF6)
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.padding(12.dp),
                tint = Color(0xFF3F51B5)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                "Student User",
                color = colors.mainText,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text("student@example.com", color = colors.subText, fontSize = 14.sp)
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray
    )
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    color: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    val colors = AnixColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = color)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = colors.mainText,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Text(text = subtitle, color = colors.subText, fontSize = 12.sp)
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Gray
        )
    }
}

@Composable
fun LogoutButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFB00020)),
        border = ButtonDefaults.outlinedButtonBorder(enabled = true)
            .copy(brush = SolidColor(Color(0xFFEEEEEE)))
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Log Out", fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(onBackClick = {}, onLogoutClick = {})
}
