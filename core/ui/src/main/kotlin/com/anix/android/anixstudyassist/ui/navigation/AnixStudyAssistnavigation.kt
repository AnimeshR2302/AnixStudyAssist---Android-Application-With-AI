package com.anix.android.anixstudyassist.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay

private const val TAG = "ANIX_Nav"

@Composable
fun AnixStudyAssistNavigation(
    modifier: Modifier = Modifier,
    sharedViewModel: SharedViewModel = hiltViewModel(LocalViewModelStoreOwner.current!!, null),
    authScreen: @Composable (AuthScreenNavigations) -> Unit,
    landingScreen: @Composable (String, LandingScreenNavigations) -> Unit,
    classDetailsScreen: @Composable (String, ClassDetailsScreenNavigations) -> Unit,
    settingsScreen: @Composable (SettingsScreenNavigations) -> Unit,
    aiChatScreen: @Composable (() -> Unit, () -> Unit) -> Unit,
    aiSettingsScreen: @Composable (() -> Unit) -> Unit,
    dataStoreScreen: @Composable (() -> Unit) -> Unit
) {
    val initialRoute = remember {
        val user = sharedViewModel.currentUser.value
        if (user != null) {
            RootGraph.Main(user)
        } else {
            RootGraph.Auth
        }
    }
    val backStack: NavBackStack<NavKey> = rememberNavBackStack(initialRoute)
    val pop: () -> Unit = {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
    }
    val setStack: () -> Unit = {
        val currentUser = sharedViewModel.currentUser.value
        Log.d(TAG, "setStack: currentUser=$currentUser, currentStackSize=${backStack.size}")
        backStack.clear()
        if (currentUser.isNullOrBlank()) {
            Log.d(TAG, "setStack: Navigating to Auth")
            backStack.add(RootGraph.Auth)
        } else {
            Log.d(TAG, "setStack: Navigating to Main (User=$currentUser)")
            backStack.add(RootGraph.Main(currentUser))
        }
    }
    val decorators = listOf(
        rememberSaveableStateHolderNavEntryDecorator(),
        rememberViewModelStoreNavEntryDecorator<NavKey>()
    )

    val entryProvider = entryProvider {
        entry<RootGraph.Auth> {
            authScreen(object : AuthScreenNavigations {
                override fun onLoginSuccess(userId: String) {
                    Log.d(TAG, "onLoginSuccess callback received for userId: $userId")
                    sharedViewModel.setCurrentUser(userId)
                    setStack.invoke()
                }
            })
        }

        entry<RootGraph.Main> { root ->
            landingScreen(root.user, object : LandingScreenNavigations {
                override fun onOpenClass(classId: String) {
                    Log.d(TAG, "onOpenClass: $classId")
                    backStack.add(MainGraph.ClassDetails(classId))
                }

                override val onOpenAiChat: () -> Unit = {
                    Log.d(TAG, "onOpenAiChat triggered")
                    backStack.add(MainGraph.AiChat)
                }
                override val onOpenSettings: () -> Unit = {
                    Log.d(TAG, "onOpenSettings triggered")
                    backStack.add(MainGraph.Settings)
                }
                override val onOpenDataStore: () -> Unit = {
                    Log.d(TAG, "onOpenDataStore triggered")
                    backStack.add(MainGraph.DataStore)
                }
                override val onOpenAddTopic: () -> Unit = {
                    Log.d(TAG, "onOpenAddTopic triggered")
                    backStack.add(MainGraph.ClassDetails("new-topic"))
                }
                override val onLogout: () -> Unit = {
                    Log.d(TAG, "onLogout triggered")
                    sharedViewModel.clearUser()
                    setStack.invoke()
                }
            })
        }

        entry<MainGraph.ClassDetails> { details ->
            classDetailsScreen(details.classId, object : ClassDetailsScreenNavigations {
                override val onBack: () -> Unit = { pop.invoke() }
                override fun onOpenSettings(classId: String) {
                    backStack.add(MainGraph.Settings)
                }

                override val onLogout: () -> Unit = {
                    sharedViewModel.clearUser()
                    setStack.invoke()
                }
            })
        }

        entry<MainGraph.AiChat> {
            aiChatScreen(
                {
                    Log.d(TAG, "AiChat back clicked")
                    pop.invoke()
                },
                {
                    Log.d(TAG, "AiChat settings clicked")
                    backStack.add(MainGraph.AiSettings)
                }
            )
        }

        entry<MainGraph.AiSettings> {
            aiSettingsScreen { pop.invoke() }
        }

        entry<MainGraph.Settings> {
            settingsScreen(object : SettingsScreenNavigations {
                override val onBack: () -> Unit = { pop.invoke() }
                override fun onOpenSetting(settingId: String) {
                    // Navigate to sub-setting if needed
                }

                override val onLogout: () -> Unit = {
                    sharedViewModel.clearUser()
                    setStack.invoke()
                }
            })
        }

        entry<MainGraph.DataStore> {
            dataStoreScreen { pop.invoke() }
        }
    }

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = { pop.invoke() },
        entryDecorators = decorators,
        entryProvider = entryProvider
    )
}
