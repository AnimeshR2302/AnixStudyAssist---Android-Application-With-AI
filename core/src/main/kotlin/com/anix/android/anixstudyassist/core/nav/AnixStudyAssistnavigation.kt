package com.anix.android.anixstudyassist.core.nav

import androidx.compose.runtime.Composable
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

@Composable
fun AnixStudyAssistNavigation(
    modifier: Modifier = Modifier,
    // Shared ViewModel scoped to the Navigation Host
    sharedViewModel: SharedViewModel = hiltViewModel(LocalViewModelStoreOwner.current!!, null),
    authScreen: @Composable (AuthScreenNavigations) -> Unit,
    landingScreen: @Composable (String, LandingScreenNavigations) -> Unit,
    classDetailsScreen: @Composable (String, ClassDetailsScreenNavigations) -> Unit,
    settingsScreen: @Composable (SettingsScreenNavigations) -> Unit
) {
    val backStack: NavBackStack<NavKey> = rememberNavBackStack(RootGraph.Auth)
    val pop: () -> Unit = {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
    }
    val setStack: () -> Unit = {
        val currentUser = sharedViewModel.currentUser.value
        backStack.clear()
        if (currentUser.isNullOrBlank()) {
            backStack.add(RootGraph.Auth)
        } else {
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
                    sharedViewModel.setCurrentUser(userId)
                    setStack.invoke()
                }
            })
        }

        entry<RootGraph.Main> { root ->
            landingScreen(root.user, object : LandingScreenNavigations {
                override fun onOpenClass(classId: String) {
                    backStack.add(MainGraph.ClassDetails(classId))
                }

                override val onOpenSettings: () -> Unit = {
                    backStack.add(MainGraph.Settings)
                }
                override val onLogout: () -> Unit = {
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
    }

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = { pop.invoke() },
        entryDecorators = decorators,
        entryProvider = entryProvider
    )
}
