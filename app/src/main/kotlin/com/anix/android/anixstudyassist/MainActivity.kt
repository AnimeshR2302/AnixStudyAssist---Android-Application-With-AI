package com.anix.android.anixstudyassist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.anix.android.anixstudyassist.aichat.presentation.ui.AiChatScreen
import com.anix.android.anixstudyassist.entry.EntryScreen
import com.anix.android.anixstudyassist.feature.datastore.presentation.ui.DataStoreScreen
import com.anix.android.anixstudyassist.feature.settings.presentation.ui.AiSettingsScreen
import com.anix.android.anixstudyassist.feature.settings.presentation.ui.SettingsScreen
import com.anix.android.anixstudyassist.topic.ui.TopicCreationScreen
import com.anix.android.anixstudyassist.topic.ui.TopicScreen
import com.anix.android.anixstudyassist.ui.navigation.AnixStudyAssistNavigation
import com.anix.android.anixstudyassist.ui.theme.AnixStudyAssistTheme
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
                EntryScreen(navigations = navigations)
            },
            landingScreen = { _, navigations ->
                LandingScreen(
                    onSubjectClick = navigations::onOpenClass,
                    onAiModeClick = navigations.onOpenAiChat,
                    onSettingsClick = navigations.onOpenSettings,
                    onDataStoreClick = navigations.onOpenDataStore,
                    onAddClick = navigations::onOpenAddTopic
                )
            },
            classDetailsScreen = { classId, navigations ->
                TopicScreen(
                    topicId = classId,
                    onBackClick = navigations.onBack,
                    onAddSubTopicClick = { navigations.onOpenAddSubTopic(classId) }
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
            topicCreationScreen = { parentTopicId, onBack ->
                TopicCreationScreen(parentTopicId = parentTopicId, onBack = onBack)
            },
            aiSettingsScreen = { onBackClick ->
                AiSettingsScreen(onBackClick = onBackClick)
            },
            dataStoreScreen = { onBackClick ->
                DataStoreScreen(onBackClick = onBackClick)
            }
        )
    }
}
