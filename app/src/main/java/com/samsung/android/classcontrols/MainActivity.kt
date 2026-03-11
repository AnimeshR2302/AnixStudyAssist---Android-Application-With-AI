package com.anix.android.anixstudyassist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.anix.android.anixstudyassist.core.nav.AnixStudyAssistNavigation
import com.anix.android.anixstudyassist.core.ui.AnixStudyAssistTheme
import com.anix.android.anixstudyassist.feature.auth.AuthScreen
import com.anix.android.anixstudyassist.feature.classdetails.ClassDetailsScreen
import com.anix.android.anixstudyassist.feature.landing.LandingScreen
import com.anix.android.anixstudyassist.feature.settings.SettingsScreen
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
    ) { baseModifier ->
        AnixStudyAssistNavigation(
            modifier = baseModifier,
            authScreen = { navigations ->
                AuthScreen(navigations = navigations)
            },
            landingScreen = { userId, navigations ->
                LandingScreen(navigations = navigations)
            },
            classDetailsScreen = { classId, navigations ->
                ClassDetailsScreen(
                    classId = classId,
                    navigations = navigations
                )
            },
            settingsScreen = { navigations ->
                SettingsScreen(navigations = navigations)
            }
        )
    }
}
