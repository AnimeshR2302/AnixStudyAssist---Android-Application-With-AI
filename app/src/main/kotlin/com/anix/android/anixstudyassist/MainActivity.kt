package com.anix.android.anixstudyassist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.anix.android.anixstudyassist.core.nav.AnixStudyAssistNavigation
import com.anix.android.anixstudyassist.core.ui.AnixStudyAssistTheme
import com.anix.android.anixstudyassist.feature.ai.presentation.ui.AiChatScreen
import com.anix.android.anixstudyassist.feature.auth.AuthScreen
import com.anix.android.anixstudyassist.feature.settings.presentation.ui.AiSettingsScreen
import com.anix.android.anixstudyassist.feature.settings.presentation.ui.SettingsScreen
import com.anix.classdetails.anixstudyassist.feature.classdetails.presentation.ui.ClassDetailsScreen
import com.anix.landing.anixstudyassist.feature.landing.presentation.ui.LandingScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnixStudyAssistApp()
        }
    }
}

@Composable
fun AnixStudyAssistApp() {
    AnixStudyAssistTheme(
        topAppBar = { /*TODO: Add a TopAppBar */ }
    ) {
        AnixStudyAssistNavigation(
            modifier = Modifier.fillMaxSize(),
            authScreen = { navigations ->
                AuthScreen(navigations = navigations)
            },
            landingScreen = { userId, navigations ->
                LandingScreen(
                    onSubjectClick = navigations::onOpenClass,
                    onAiModeClick = navigations.onOpenAiChat,
                    onSettingsClick = navigations.onOpenSettings,
                    onAddClick = {}
                )
            },
            classDetailsScreen = { classId, navigations ->
                ClassDetailsScreen(
                    subjectName = classId.toReadableSubjectName(),
                    onBackClick = navigations.onBack,
                    onMenuClick = { navigations.onOpenSettings(classId) }
                )
            },
            settingsScreen = { navigations ->
                SettingsScreen(
                    onBackClick = navigations.onBack,
                    onLogoutClick = navigations.onLogout,
                    onSettingClick = navigations::onOpenSetting
                )
            },
            aiChatScreen = { onBackClick, onSettingsClick ->
                AiChatScreen(onBackClick = onBackClick, onSettingsClick = onSettingsClick)
            },
            aiSettingsScreen = { onBackClick ->
                AiSettingsScreen(onBackClick = onBackClick)
            }
        )
    }
}

private fun String.toReadableSubjectName(): String {
    return split('-', '_')
        .filter { it.isNotBlank() }
        .joinToString(" ") { part ->
            part.lowercase().replaceFirstChar { it.uppercase() }
        }
        .ifBlank { this }
}
