package com.anix.android.anixstudyassist.core.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    val decorators = listOf(
        rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
        rememberViewModelStoreNavEntryDecorator<NavKey>()
    )

    val currentUser by sharedViewModel.currentUser.collectAsState()

    val entryProvider = entryProvider<NavKey> {
        entry<RootGraph.Auth> {
            authScreen(object : AuthScreenNavigations {
                override fun onLoginSuccess(userId: String) {
                    sharedViewModel.setCurrentUser(userId)
                    backStack.setStack(listOf(RootGraph.Main(userId)))
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
                    backStack.setStack(listOf(RootGraph.Auth))
                }
            })
        }

        entry<MainGraph.ClassDetails> { details ->
            classDetailsScreen(details.classId, object : ClassDetailsScreenNavigations {
                override val onBack: () -> Unit = { backStack.pop() }
                override fun onOpenSettings(classId: String) {
                    backStack.add(MainGraph.Settings)
                }

                override val onLogout: () -> Unit = {
                    sharedViewModel.clearUser()
                    backStack.setStack(listOf(RootGraph.Auth))
                }
            })
        }

        entry<MainGraph.Settings> {
            settingsScreen(object : SettingsScreenNavigations {
                override val onBack: () -> Unit = { backStack.pop() }
                override fun onOpenSetting(settingId: String) {
                    // Navigate to sub-setting if needed
                }

                override val onLogout: () -> Unit = {
                    sharedViewModel.clearUser()
                    backStack.setStack(listOf(RootGraph.Auth))
                }
            })
        }
    }

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = { backStack.pop() },
        entryDecorators = decorators,
        entryProvider = entryProvider
    )
}

private fun NavBackStack<NavKey>.pop() {
    // Implementation of pop if needed, or use backStack.pop() if it exists in NavBackStack
}

private fun NavBackStack<NavKey>.setStack(newStack: List<NavKey>) {
    // Implementation of setStack if needed
}
